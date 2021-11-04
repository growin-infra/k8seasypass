package app.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.model.Data;

@Repository
public interface DataPagerRepository extends PagingAndSortingRepository<Data, Integer> {

}
