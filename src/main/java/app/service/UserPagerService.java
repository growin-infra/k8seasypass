package app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import app.model.User;
import app.repo.UserPagerRepository;

@Service
public class UserPagerService {

	private UserPagerRepository userPagerRepository;
	
	public UserPagerService(UserPagerRepository userPagerRepository) {
		this.userPagerRepository = userPagerRepository;
	}
	
	public Page<User> findAllPageable(Pageable pageable) {
        return userPagerRepository.findAll(pageable);
	}
	
}
