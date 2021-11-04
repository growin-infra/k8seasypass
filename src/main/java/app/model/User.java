package app.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String pass;
	private Integer auth;
	private Integer failcnt;
	private LocalDateTime time;
	
	public User() {
		final LocalDateTime now = LocalDateTime.now();
		time = now;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", pass=" + pass + ", auth=" + auth + ", failcnt=" + failcnt + ", time=" + time + "]";
	}
}
