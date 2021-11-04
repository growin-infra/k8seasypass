package app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import app.model.Data;
import app.repo.DataPagerRepository;

@Service
public class DataPagerService {

	private DataPagerRepository dataPagerRepository;
	
	public DataPagerService(DataPagerRepository dataPagerRepository) {
		this.dataPagerRepository = dataPagerRepository;
	}
	
	public Page<Data> findAllPageable(Pageable pageable) {
        return dataPagerRepository.findAll(pageable);
	}
	
}
