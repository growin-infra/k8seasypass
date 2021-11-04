package app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import app.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("from User")
	public List<User> findByAll();

	@Query("from User where id=?1")
	public User findByID(int id);

	@Transactional
	@Modifying
	@Query("update User a set a.email = ?2, a.pass = ?3, a.auth = ?4 where a.id=?1")
	public int modifyByUserID(int id, String email, String pass, int auth);
	
	@Transactional
	@Modifying
	@Query("update User a set a.failcnt = (select max(b.failcnt)+1 from User b where b.id=?1) where a.id=?1")
	public int updateByMaxFailcnt(int id);
	
	@Transactional
	@Modifying
	@Query("update User a set a.failcnt = 0 where a.id=?1")
	public int updateByInitFailcnt(int id);
	
	@Transactional
	@Modifying
	@Query("update User a set a.pass = ?1 where a.id=1")
	public int updateByAdminPass(String pass);
	
//	@Transactional
//	@Modifying
//	@Query("update User a set a.cert = ?2 where a.id=?1")
//	public int updateByCert(int id, int cert);
	
//	@Transactional
//	@Modifying
//	@Query("update User a set a.cert = 0")
//	public int updateByCertZero();
	
	@Query("from User where id=1 and pass=?1")
	public User findByAdmin(String pw);
	
	@Query("from User where email=?1")
	public User findByUser(String email);
	
	@Query("from User where email=?1")
	public List<User> findByEMAIL(String email);
	
	@Query("from User where email=?1 and pass=?2")
	public User findByUsernamePassword(String username,String password);
	
	@Query("from User where id = (select max(id) from User)")
	public User findByMaxUserid();

}
