package app.controller;

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

import app.model.Data;
import app.model.Pager;
import app.model.User;
import app.repo.DataRepository;
import app.repo.UserRepository;
import app.service.CommonService;
import app.service.DataPagerService;
import app.service.UserPagerService;

@Controller
public class DataController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int BUTTONS_TO_SHOW = 15;
	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 15;
	private static final int[] PAGE_SIZES = {15, 30, 60};
	
	@Autowired
	DataRepository dataRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CommonService commonService;

	@Autowired
	DataPagerService dataPagerService;
	
	@RequestMapping(value = "/data/list", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView dataList(@RequestParam("pageSize") Optional<Integer> pageSize,
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
				mv.setViewName("page/data/list");
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			
			int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);
	        
	        Page<Data> datalist = dataPagerService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
	        Pager pager = new Pager(((Page<Data>) datalist).getTotalPages(), ((Slice<Data>) datalist).getNumber(), BUTTONS_TO_SHOW);
	        
			if (datalist != null && datalist.getSize() > 0) {
				modelMap.put("list", datalist);
			}
			modelMap.put("selectedPageSize", evalPageSize);
			modelMap.put("pageSizes", PAGE_SIZES);
			modelMap.put("pager", pager);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/data/addform", method = {RequestMethod.GET, RequestMethod.POST})
	public String data_addform(HttpServletRequest request, ModelMap modelMap) {
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
		return "page/data/addform";
	}
	
	@RequestMapping(value = "/data/add", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView data_add(HttpServletRequest request, Data data, ModelMap modelMap) {
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
				
				commonService.data_save(data);
				
				modelMap.put("message", "Data has been successfully saved.");
				mv.setViewName("redirect:/data/list");
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	

	@RequestMapping(value = "/data/modifyform", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView data_modifyform(@RequestParam("pageSize") Optional<Integer> pageSize,
				 						@RequestParam("page") Optional<Integer> page,
				 						@RequestParam("id") String id, HttpServletRequest request, ModelMap modelMap) {
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
				
				Data data = dataRepository.findByID(Integer.valueOf(id));
				if (data != null) {
					int mid = data.getId();
					String mname = data.getName();
					String murl = data.getUrl();
					String mtoken = data.getToken();
					String maid = data.getAid();
					String mapw = data.getApw();
					
					mv.addObject("id", mid);
					mv.addObject("name", mname);
					mv.addObject("url", murl);
					mv.addObject("token", mtoken);
					mv.addObject("aid", maid);
					mv.addObject("apw", mapw);
				}
				
				mv.setViewName("page/data/modifyform");
				
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
	
	@RequestMapping(value = "/data/modify", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView data_modify(@RequestParam("pageSize") int pageSize,
									@RequestParam("page") int page,
									Data data, HttpServletRequest request, ModelMap modelMap) {
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
				
				int mid = data.getId();
				String mname = data.getName();
				String murl = data.getUrl();
				String mtoken = data.getToken();
				String maid = data.getAid();
				String mapw = data.getApw();
				
				commonService.data_modify(mid, mname, murl, mtoken, maid, mapw);
				
				modelMap.put("message", "Data has been successfully modify.");
				mv.setViewName("redirect:/data/list/?pageSize="+pageSize+"&page="+page);
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
	
	@RequestMapping(value = "/data/delete", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView data_delete(@RequestParam("id") int id, Data data, Model model, HttpServletRequest request, ModelMap modelMap) {
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
				
				commonService.data_delete(id);
				
				modelMap.put("message", "Data has been successfully delete.");
				mv.setViewName("redirect:/data/list");
			} else {
				modelMap.put("s_error", "The session has timeout.");
				mv.setViewName("redirect:/");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	
	/*
	@RequestMapping(value = "/main", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView main(@RequestParam(value="user", required=false) String user, Model model, HttpServletRequest request, HttpSession session) throws IOException {
		ModelAndView mv = new ModelAndView();
		BufferedReader br = null;
		File file = null;
		List<Map<String, Object>> resendList = null;
		String getUser = "user";
		try {
			if (user != null && !"".equals(user)) {
				getUser = user;
			}
			session.setAttribute("user", getUser);
			String filefullname = homedir + separator + fullpath + separator + filename;
			file = new File(filefullname);
			if (file.isFile()) {
				resendList = new ArrayList<Map<String, Object>>();
				br = new BufferedReader(new FileReader(filefullname));

				String line;
				String jsonData = "";
				while ((line = br.readLine()) != null) {
					jsonData = line;
				}
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray) parser.parse(jsonData);
				for(int i=0; i<array.size(); i++) {
					resendList.add((Map<String, Object>) array.get(i));
				}
				model.addAttribute("list", resendList);
				
			} else {
				model.addAttribute("msg", "File does not exist!!");
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) br.close();
		}
		mv.setViewName("page/"+getUser);
		return mv;
	}
	
	
	@GetMapping("/ajax_save")
	@ResponseBody
	public ModelAndView ajax_save(@RequestParam String jsonData, HttpServletRequest request, Model model) throws IOException {
		ModelAndView mv = new ModelAndView("jsonView");
		FileWriter fw = null;
		File file = null;
		JSONParser parser = null;
		JSONArray array = null;
		try {
			String filefullname = homedir + separator + "data" + separator + filename;
			parser = new JSONParser();
			array = (JSONArray) parser.parse(jsonData);

			file = new File(filefullname);
			if (!file.isFile()) {
				file.createNewFile();
			}
			fw = new FileWriter(filefullname);
			fw.write(array.toJSONString());

			model.addAttribute("msg", "Success");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fw.close();
		}
		return mv;
	}
	
	@RequestMapping(value = "/save", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView save(@RequestParam(value = "id_data", required = false) String jsonData, Model model, HttpServletRequest request) throws IOException {
		ModelAndView mv = new ModelAndView();
		FileWriter fw = null;
		File file = null;
		JSONParser parser = null;
		JSONArray array = null;
		try {
			String filefullname = homedir + separator + "data" + separator + filename;
			parser = new JSONParser();
			array = (JSONArray) parser.parse(jsonData);

			file = new File(filefullname);
			if (!file.isFile()) {
				file.createNewFile();
			}
			fw = new FileWriter(filefullname);
			fw.write(array.toJSONString());

			model.addAttribute("result", "Success");
//			mv.addObject("result", "Success");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fw.close();
		}
		mv.setViewName("redirect:/main");
		return mv;
	}
	*/
	
	@RequestMapping(value = "/data/admin_cert", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView admin_cert(@RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
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
						
						mv.addObject("row_index", param.get("row_index"));
						mv.addObject("site_pass", param.get("site_pass"));
						mv.addObject("site_id", param.get("site_id") != null ? param.get("site_id") : "");
						mv.addObject("site_aid", param.get("site_aid") != null ? param.get("site_aid") : "");
						mv.addObject("site_url", param.get("site_url") != null ? param.get("site_url") : "");
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
	
	@RequestMapping(value = "/data/admin_cert3", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView admin_cert3(@RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
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
						mv.addObject("site_id", param.get("site_id"));
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
	
	
	@RequestMapping(value = "/data/changepass", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView change_pass(@RequestParam Map<String, String> param, HttpServletRequest request, ModelMap modelMap) {
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
				
				if (param != null && param.get("site_id") != null) {
					int id = Integer.valueOf(param.get("site_id"));
					String aid = param.get("site_aid") != null ? param.get("site_aid").toString() : "";
					String apw = param.get("site_pass") != null ? param.get("site_pass").toString() : "";
					
					commonService.data_modify_idpw(id, aid, apw);
					
					mv.addObject("row_index", param.get("row_index"));
					mv.addObject("site_id", param.get("site_id"));
					mv.addObject("site_pass", param.get("site_pass") != null ? param.get("site_pass") : "");
					mv.addObject("site_aid", param.get("site_aid") != null ? param.get("site_aid") : "");
					mv.addObject("site_url", param.get("site_url") != null ? param.get("site_url") : "");
					mv.addObject("result", "Success");
					
				} else {
					mv.addObject("result", "Update failed");
				}
				
			} else {
				modelMap.put("s_error", "The session has timeout.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	
	
}
