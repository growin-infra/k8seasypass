package app.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Data {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;
	private String url;
	@Column(length = 3000)
	private String token;
	private String aid;
	private String apw;
	private LocalDateTime time;
	
	public Data() {
		final LocalDateTime now = LocalDateTime.now();
		time = now;
	}
	
	@Override
	public String toString() {
		return "Data [id=" + id + ", name=" + name + ", url=" + url + ", token=" + token + ", aid=" + aid + ", apw=" + apw + ", time=" + time + "]";
	}
}
