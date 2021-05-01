package com.cos.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

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
//함수의 실행 직전과 직후를 관리할수 있음 AOP
public class Dispatcher implements Filter{
	boolean isMatching = false;

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
//			System.out.println(requestMapping.value());
			if(requestMapping.value().equals(endPoint)) {
				isMatching = true;
				try {
					//파라미터 분석
					Parameter[] params = method.getParameters();
					String path = null;
					if(params.length != 0) {
//						System.out.println("params[0].getType() : " + params[0].getType());
						Object dtoInstance = params[0].getType().newInstance(); // 해당 오브젝트를 리플렉션해서 set함수 호출
//						String username = request.getParameter("username");
//						String password = request.getParameter("password");
//						System.out.println("username : " + username);
//						System.out.println("password : " + password);
						
						// keys값을 변형 ex:username => setUsername
						setData(dtoInstance, request);
						path = (String) method.invoke(userController, dtoInstance);
					} else {
						path = (String) method.invoke(userController);
					}
					
					RequestDispatcher dis = request.getRequestDispatcher(path); //필터를 다시 안탐!! requestsend redirect는 다시 톰캣을 탐
					dis.forward(request, response); // "/"로 포워딩
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if(!isMatching) {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("잘못된 주소 요청입니다. 404에러");
			out.flush();
		}
	}
	
	private <T> void setData(T instance, HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames();//키값 
		while(keys.hasMoreElements()) { //값이 있으면 열거형 타입
			String key = (String) keys.nextElement();
			String methodKey = keyToMethodKey(key);
			
			Method[] methods = instance.getClass().getDeclaredMethods(); //선언된 메서드 dto들
			for (Method method : methods) { //dto내의 함수 toString ,set, get들
				if(method.getName().equals(methodKey)) {
					try {
						if("id".equals(key)) { //id는 int형임
							method.invoke(instance, Integer.valueOf(request.getParameter(key)));
						} else {
							method.invoke(instance, request.getParameter(key));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
			
		}
	}
	
	private String keyToMethodKey(String key) {
		String firstKey = "set";
		String upperKey = key.substring(0, 1).toUpperCase();
		String remainKey = key.substring(1);
		
		return firstKey + upperKey + remainKey;
	}

}
