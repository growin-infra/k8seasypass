package app.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.model.User;

@Repository
public interface UserPagerRepository extends PagingAndSortingRepository<User, Integer> {

}
