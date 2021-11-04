package app.repo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import app.model.Data;

public interface DataRepository extends JpaRepository<Data, Integer> {
	
	@Query("from Data")
	public Page<Data> findAllPageable(Pageable pageable);
	
	@Query("from Data")
	public Page<Data> findAll(Pageable pageable);
	
	@Query("from Data")
	public List<Data> findByAll();
	
	@Query("from Data where id=?1")
	public Data findByID(int id);
	
	@Transactional
	@Modifying
	@Query("update Data a set a.name = ?2, a.url = ?3, a.token = ?4, a.aid = ?5, a.apw = ?6 where a.id=?1")
	public int modifyByDataID(int id, String name, String url, String token, String aid, String apw);
	
	@Transactional
	@Modifying
	@Query("update Data a set a.aid = ?2, a.apw = ?3 where a.id=?1")
	public int modifyByDataIDPW(int id, String aid, String apw);
	
	@Query("select count(1) from Data")
	public int findAllCnt();
	
	
//	@Query("select b from (select rownum() as num, a from Data a) b where b.num > ?1 and b.num < ?2")
	@Query("select a from Data a")
	public List<Data> findListPaging(int startIndex, int pageSize);
	
}
