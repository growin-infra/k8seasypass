package app.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import app.model.Data;
import app.model.User;
import app.repo.DataPagerRepository;
import app.repo.DataRepository;
import app.repo.UserRepository;

@Service
public class CommonService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	DataRepository dataRepository;
	
	@Autowired
	DataPagerRepository dataPagerRepository;
	
	private String separator = System.getProperty("file.separator");
	
	@Value("${backup.file.path}")
	String fullpath;
	
	@Value("${backup.file.name.data}")
	String datafilename;
	
	@Value("${backup.file.name.user}")
	String userfilename;
	
	public void init_data() throws IOException {
		File d_file = null;
		File u_file = null;
		try {
			String ufile = fullpath + separator + userfilename;
			u_file = new File(ufile);
			if (!u_file.isFile()) {
				User user = new User();
				user.setId(1);
				user.setEmail("admin@growin.co.kr");
				user.setPass("1");
				user.setAuth(1);
				user.setFailcnt(0);
				user.setTime(LocalDateTime.now());
				
				userRepository.save(user);
			}
			
			String dfile = fullpath + separator + datafilename;
			d_file = new File(dfile);
			if (!d_file.isFile()) {
				Data data = new Data();
				data.setId(1);
				data.setName("dashboard");
				data.setUrl("http://");
				data.setTime(LocalDateTime.now());
				
				dataRepository.save(data);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void data_insert() throws IOException {
		File file = null;
		FileWriter fw = null;
		try {
			String filefullname = fullpath + separator + datafilename;
			file = new File(filefullname);
			if (!file.isFile()) {
				file.createNewFile();
			}
			fw = new FileWriter(filefullname);
			
			List<Data> listall = dataRepository.findByAll();
			for (int i=0; i<listall.size(); i++) {
				String str = "insert into Data (id,name,url,token,aid,apw,time) values (";
				str += listall.get(i).getId();
				str += ",'" + listall.get(i).getName();
				str += "','" + listall.get(i).getUrl();
				str += "','" + listall.get(i).getToken();
				str += "','" + listall.get(i).getAid();
				str += "','" + listall.get(i).getApw();
				str += "','" + listall.get(i).getTime();
				str += "');\n";
				fw.write(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) fw.close();
		}
	}
	
	public void user_insert() throws IOException {
		File file = null;
		FileWriter fw = null;
		try {
			String filefullname = fullpath + separator + userfilename;
			file = new File(filefullname);
			if (!file.isFile()) {
				file.createNewFile();
			}
			fw = new FileWriter(filefullname);
			
			List<User> listall = userRepository.findByAll();
			for (int i=0; i<listall.size(); i++) {
				String str = "insert into User (id,email,pass,auth,failcnt,time) values (";
				str += listall.get(i).getId();
				str += ",'" + listall.get(i).getEmail();
				str += "','" + listall.get(i).getPass();
				str += "'," + listall.get(i).getAuth();
				str += "," + listall.get(i).getFailcnt();
				str += ",'" + listall.get(i).getTime();
				str += "');\n";
				fw.write(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) fw.close();
		}
	
	}
	
//	public void cert_update(int id, int cert) {
//		try {
//			userRepository.updateByCert(id, cert);
//			user_insert();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	public void cert_update_all() {
//		try {
//			userRepository.updateByCertZero();
//			user_insert();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void max_failcnt(int id) {
		try {
			userRepository.updateByMaxFailcnt(id);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void failcnt_init(int id) {
		try {
			userRepository.updateByInitFailcnt(id);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void data_save(Data data) {
		try {
			dataRepository.save(data);
			data_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void data_modify(int id, String name, String url, String token, String aid, String apw) {
		try {
			dataRepository.modifyByDataID(id, name, url, token, aid, apw);
			data_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void data_delete(int id) {
		try {
			dataRepository.deleteById(id);
			data_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void data_modify_idpw(int id, String aid, String apw) {
		try {
			dataRepository.modifyByDataIDPW(id, aid, apw);
			data_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void user_save(User user) {
		try {
			userRepository.save(user);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void user_modify(int id, String email, String pass, int auth) {
		try {
			int m = userRepository.modifyByUserID(id, email, pass, auth);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void user_delete(int id) {
		try {
			userRepository.deleteById(id);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void adminpass_init(String pass) {
		try {
			userRepository.updateByAdminPass(pass);
			user_insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
