package com.cos.reflect.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.anno.RequestMapping;
import com.cos.reflect.controller.UserController;

//분기 시키기 (Router의 역할)
public class Dispatcher implements Filter{

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
//		System.out.println("컨텍스트패스 : " + request.getContextPath()); //프로젝트 시작주소
//		System.out.println("식별자주소 : " + request.getRequestURI()); //끝주소
//		System.out.println("전체주소 : " + request.getRequestURL()); //전체주소
		
		// /user파싱하기
		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("엔드포인트: " +endPoint);
		
		UserController userController = new UserController();
//		if(endPoint.equals("/join")) {
//			userController.join();
//		} else if(endPoint.equals("/login")) {
//			userController.login();
//		}
		
		// 리플렉션 -> 메서드를 런타임 시점에서 찾아내 실행
		Method[] methods = userController.getClass().getDeclaredMethods();
//		for (Method method : methods) {
////			System.out.println(method.getName());
//			if(endPoint.equals("/"+method.getName())) {
//				try {
//					method.invoke(userController);	
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
		for (Method method : methods) { // 4바퀴 (join, login, user, hello)
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping) annotation; //다운 캐스팅 이렇게하면 바로 RequestMapping을 new한것보다 사용할수 있는게 많음
			System.out.println(requestMapping.value());
			
			if(requestMapping.value().equals(endPoint)) {
				try {
					String path = (String) method.invoke(userController);
					RequestDispatcher dis = request.getRequestDispatcher(path);
					dis.forward(request, response);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		
		
	}

}
