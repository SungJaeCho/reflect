package com.cos.reflect.controller.dto;

public class JoinDto { //vo와 dto의 차이점은 dto는 화면에서 가져올때 꼭 필요한 것만 가져오는 용도로 만들어야 나중에 밸리데이션 체크라던지 null오류에 있어서 용이함, 리플렉션할때도 좋음
	private String username;
	private String password;
	private String email;
	
	
	@Override
	public String toString() {
		return "JoinDto [username=" + username + ", password=" + password + ", email=" + email + ", toString()=" + "]";
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
}
