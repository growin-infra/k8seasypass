package app.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import app.model.User;
import app.repo.UserRepository;
import app.service.CommonService;

@Controller
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CommonService commonService;

	@Value("${root.pass}")
	String initpass;
	
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
	public String index(Model model, HttpServletRequest request) throws Exception {
//		logger.trace("trace");
//		logger.debug("debug");
//		logger.info("info");
//		logger.warn("warn");
//		logger.error("error");
		
		commonService.init_data();
		
		return "login";
	}
	
	@RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
	public String login_user(@RequestParam(value="username", required=false) String username, @RequestParam(value="password", required=false) String password,
			HttpServletRequest request, ModelMap modelMap) {

		String url = "";
		HttpSession session = request.getSession(true);
		User user = (User) session.getAttribute("USER");
		if (user != null) {
			url = "redirect:/data/list";
		} else {
			
			User suser = userRepository.findByUser(username);
			if (suser != null) {
				
				int sid = suser.getId();
				String semail = suser.getEmail();
				String spass = suser.getPass();
				int sauth = suser.getAuth();
				int sfailcnt = suser.getFailcnt();
				
				if (semail.equals(username) && spass.equals(password)) {
					
					session.setAttribute("USER", suser);
					modelMap.put("session_id", sid);
					modelMap.put("session_email", semail);
					modelMap.put("session_auth", sauth);
					commonService.failcnt_init(sid);
					url = "redirect:/data/list";
					
				} else {
					
					if (sid == 1) {	//admin
						if (sfailcnt >= 3) {
							//modelMap.put("error", "Admin Account Init");
							modelMap.put("username", username);
							modelMap.put("error", "init");
							logger.debug("Login failed more than 3 times with ID \""+username+"\"");
						} else {
							commonService.max_failcnt(suser.getId());
							//modelMap.put("error", "Invalid Account");
							modelMap.put("username", username);
							modelMap.put("error", "account");
						}
					} else {
						if (sfailcnt >= 3) {
							modelMap.put("username", username);
							modelMap.put("error", "admin");
						} else {
							commonService.max_failcnt(suser.getId());
							//modelMap.put("error", "Invalid Account");
							modelMap.put("username", username);
							modelMap.put("error", "account");
						}
					}
					url = "login";
				}
			} else {
				//modelMap.put("error", "Invalid Account");
				modelMap.put("username", username);
				modelMap.put("error", "nothing");
				url = "login";
			}
		}
		return url;
	}
	
	@RequestMapping(value = "/initpass", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView init_pass(@RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
		ModelAndView mv = new ModelAndView("jsonView");
		try {
			if (initpass.equals(param.get("init_pw"))) {
				mv.addObject("result", "Certified Succeeded");
				
				commonService.adminpass_init(initpass);
				
			} else {
				mv.addObject("result", "Certified failed");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	public String logout_user(HttpSession session) {
		session.removeAttribute("USER");
		session.invalidate();
		return "redirect:/";
	}
	
}
