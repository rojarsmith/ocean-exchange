package io.aext.core.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.aext.core.base.model.vo.ResultVO;
import io.aext.core.base.service.LocaleMessageSourceService;
import io.aext.core.base.util.IpUtils;

/**
 * @author Rojar Smith
 *
 * @date 2021-06-30
 */
public class BaseController {
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Autowired
	LocaleMessageSourceService localeMessageSourceService;

	protected Map<String, List<Map<String, String>>> buildBindingResultData(BindingResult bindingResult) {
		Map<String, List<Map<String, String>>> data = new HashMap<>();
		List<Map<String, String>> errors = new ArrayList<>();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			Map<String, String> error = new HashMap<>();
			error.put("type", "field");
			error.put("name", fieldError.getField());
			error.put("message", fieldError.getDefaultMessage());
			errors.add(error);
		}
		for (ObjectError objectError : bindingResult.getGlobalErrors()) {
			Map<String, String> error = new HashMap<>();
			error.put("type", "global");
			error.put("name", objectError.getCode());
			error.put("message", objectError.getDefaultMessage());
			errors.add(error);
		}
		data.put("errors", errors);
		return data;
	}

	/*
	 * Get mapping path by method name.
	 */
	public String getRequestMappingPath(String target) {
		Map<RequestMappingInfo, HandlerMethod> methods = applicationContext.getBean(RequestMappingHandlerMapping.class)
				.getHandlerMethods().entrySet().stream().filter(x -> x.getValue().toString().contains(target))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (!methods.entrySet().iterator().hasNext()) {
			return "";
		}

		String key = methods.entrySet().iterator().next().getKey().getPatternsCondition().toString();
		key = key.substring(1, key.length() - 1);

		return key;
	}

	protected String getMessageML(String message) {
		try {
			return localeMessageSourceService.getMessage(message);
		} catch (Exception e) {
			e.toString();
			return "No message.";
		}
	}

	protected String getIpAddress() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return IpUtils.getIpAddr(attributes.getRequest());
	}

	public boolean equalsN(String value) {
		if (!value.equals("N")) {
			return false;
		}
		return true;
	}

	public void throwResponseStatusException(String messageSymbol) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessageML(messageSymbol));
	}

	protected ResponseEntity<?> success() {
		return success(getMessageML("SUCCESS"));
	}

	protected ResponseEntity<?> success(String msg) {
		return success(msg, null);
	}

	protected ResponseEntity<?> success(Object obj) {
		return success(getMessageML("SUCCESS"), obj);
	}

	protected ResponseEntity<?> success(String msg, Object obj) {
		return ResponseEntity.ok().body(new ResultVO<Object>(msg, obj));
	}

	protected ResponseEntity<?> error(String msg) {
		return error(msg, null);
	}

	protected ResponseEntity<?> error(String msg, Object obj) {
		return ResponseEntity.badRequest().body(new ResultVO<Object>(msg, obj));
	}

	protected String readRedisValueAsString(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		return Optional.ofNullable(valueOperations.get(key)).orElse("N");
	}

	protected void updateRedisValueAsString(String key, String value, int seconds) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(key, value, seconds, TimeUnit.SECONDS);
	}

	protected void deleteRedisValueAsString(String key) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		valueOperations.getOperations().delete(key);
	}
}
