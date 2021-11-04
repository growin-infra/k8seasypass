package app.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.model.Pager;
import app.model.User;
import app.repo.UserRepository;
import app.service.CommonService;
import app.service.UserPagerService;

@Controller
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int BUTTONS_TO_SHOW = 9;
	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 9;
	private static final int[] PAGE_SIZES = {9, 18, 36};
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	UserPagerService userPagerService;
	
	@RequestMapping(value = "/user/list", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView userlist(@RequestParam("pageSize") Optional<Integer> pageSize,
			 					 @RequestParam("page") Optional<Integer> page,
			 					 HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView();
		try {
			HttpSession session = request.getSession(true);
			User user = (User) session.getAttribute("USER");
			if (user != null) {
				String email = user.getEmail();
				int sid = user.getId();
				int auth = user.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				mv.setViewName("page/user/list");
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			
			int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);
	        
	        Page<User> listall = userPagerService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
	        Pager pager = new Pager(((Page<User>) listall).getTotalPages(), ((Slice<User>) listall).getNumber(), BUTTONS_TO_SHOW);
			
	        if (listall != null && listall.getSize() > 0) {
				mv.addObject("list", listall);
			}
			mv.addObject("selectedPageSize", evalPageSize);
			mv.addObject("pageSizes", PAGE_SIZES);
			mv.addObject("pager", pager);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/user/addform", method = {RequestMethod.GET, RequestMethod.POST})
	public String user_addform(Model model, HttpServletRequest request, ModelMap modelMap) {
		try {
			HttpSession session = request.getSession(true);
			User user = (User) session.getAttribute("USER");
			if (user != null) {
				String email = user.getEmail();
				int sid = user.getId();
				int auth = user.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
			} else {
				modelMap.put("s_error", "The session has timeout.");
				return "redirect:/";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "page/user/addform";
	}
	
	@RequestMapping(value = "/user/add", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView user_add(@RequestParam Map<String, String> param, HttpServletRequest request, Model model, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView("jsonView");
		try {
			HttpSession session = request.getSession(true);
			User auser = (User) session.getAttribute("USER");
			if (auser != null) {
				String email = auser.getEmail();
				int sid = auser.getId();
				int auth = auser.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				List<User> userlist = userRepository.findByEMAIL(param.get("email"));
				if (userlist.size() == 0) {
					User setuser = new User();
					setuser.setEmail(param.get("email"));
					setuser.setPass(param.get("pass"));
					setuser.setAuth(Integer.valueOf(param.get("auth")));
					setuser.setFailcnt(0);;
					commonService.user_save(setuser);
					mv.addObject("result", "Successfully");
				} else {
					mv.addObject("result", "ID Duplicate");
				}
				
			} else {
				mv.addObject("result", "The session has timeout.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/user/modifyform", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView user_modifyform(@RequestParam("pageSize") Optional<Integer> pageSize,
										@RequestParam("page") Optional<Integer> page,
										@RequestParam("id") String id, HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView();
		try {
			HttpSession session = request.getSession(true);
			User auser = (User) session.getAttribute("USER");
			if (auser != null) {
				String email = auser.getEmail();
				int sid = auser.getId();
				int auth = auser.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				User user = userRepository.findByID(Integer.valueOf(id));
				if (user != null) {
					int mid = user.getId();
					String memail = user.getEmail();
					String mpass = user.getPass();
					int mauth = user.getAuth();
					
					mv.addObject("id", mid);
					mv.addObject("email", memail);
					mv.addObject("pass", mpass);
					mv.addObject("auth", mauth);
				}
				
				mv.setViewName("page/user/modifyform");
				
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			modelMap.put("pageSize", pageSize);
			modelMap.put("page", page);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping(value = "/user/modify", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView modify(@RequestParam("pageSize") int pageSize,
							   @RequestParam("page") int page,
							   @RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView("jsonView");
		try {
			HttpSession session = request.getSession(true);
			User auser = (User) session.getAttribute("USER");
			if (auser != null) {
				int sid = auser.getId();
				String email = auser.getEmail();
				int auth = auser.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				int mid = Integer.valueOf(param.get("id"));
				String memail = param.get("email");
				String mpass = param.get("pass");
				int mauth = Integer.valueOf(param.get("auth"));
				
				commonService.user_modify(mid, memail, mpass, mauth);
				
				mv.addObject("result", "Successfully");
				
			} else {
				mv.addObject("result", "The session has timeout.");
			}
			mv.addObject("pageSize", pageSize);
			mv.addObject("page", page);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/user/delete", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView user_delete(@RequestParam("id") int id, User user, HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView();
		
		try {
			HttpSession session = request.getSession(true);
			User auser = (User) session.getAttribute("USER");
			if (auser != null) {
				String email = auser.getEmail();
				int sid = auser.getId();
				int auth = auser.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				
				commonService.user_delete(id);
				
				mv.addObject("message", "Data has been successfully delete.");
				mv.setViewName("redirect:/user/list");
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/user/admin_cert", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView user_admin_cert(@RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView("jsonView");
		try {
			HttpSession session = request.getSession(true);
			User auser = (User) session.getAttribute("USER");
			if (auser != null) {
				String email = auser.getEmail();
				int sid = auser.getId();
				int auth = auser.getAuth();
				modelMap.put("session_id", sid);
				modelMap.put("session_email", email);
				modelMap.put("session_auth", auth);
				
				if (param != null && param.get("admin_pw") != null) {
					User sadmin = userRepository.findByAdmin(param.get("admin_pw"));
					if (sadmin != null) {
						mv.addObject("result", "Certified");
					} else {
						mv.addObject("result", "Invalid admin");
					}
				} else {
					mv.addObject("result", "Invalid info");
				}
			} else {
				mv.addObject("result", "The session has timeout.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
}
