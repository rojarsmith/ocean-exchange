package io.aext.core.service.controller;

import static io.aext.core.base.constant.SystemConstant.CAPTCHA_TOKEN_PREFIX;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.aext.core.base.controller.BaseController;
import io.aext.core.base.security.LimitedAccess;
import io.aext.core.base.service.DataCacheService;
import io.aext.core.base.util.CaptchaLite;

/**
 * @author Rojar Smith
 * @Description:
 * @date 2021/07/02
 */
@RestController
@RequestMapping(value = { "/api/v1/captcha" })
public class CaptchaController extends BaseController {
	@Autowired
	DataCacheService dataCacheService;

	@PostMapping(value = { "/new/png" }, produces = "image/png")
	@ResponseBody
	@LimitedAccess(frequency = 1, second = 1, heavyFrequency = 10, heavySecond = 60, heavyDelay = 86400)
	public BufferedImage newImage() {
		// Check cache
		String ip = getIpAddress();
		String keyToken = CAPTCHA_TOKEN_PREFIX + ip;

		// Generate captcha
		CaptchaLite validateCode = new CaptchaLite();
		List<Object> captcha = validateCode.getRandomCodeImage();
		String token = (String) captcha.get(0);
		BufferedImage image = (BufferedImage) captcha.get(1);

		// Update cache
		dataCacheService.update(keyToken, token, 600);

		return image;
	}

	@PostMapping(value = { "/new/base64" })
	@ResponseBody
	@LimitedAccess(frequency = 1, second = 1, heavyFrequency = 10, heavySecond = 60, heavyDelay = 86400)
	public ResponseEntity<?> newBase64() {
		// Check cache
		String ip = getIpAddress();
		String keyToken = CAPTCHA_TOKEN_PREFIX + ip;

		// Generate captcha
		CaptchaLite validateCode = new CaptchaLite();
		List<Object> captcha = validateCode.getRandomCodeBase64();
		String token = (String) captcha.get(0);
		String image = (String) captcha.get(1);
		Map<String, String> data = new HashMap<>();
		data.put("image", "data:image/png;base64," + image);

		// Update cache
		dataCacheService.update(keyToken, token, 600);

		return success(data);
	}

	@Bean
	public HttpMessageConverter<BufferedImage> bufferedImageHttpMessageConverter() {
		return new BufferedImageHttpMessageConverter();
	}
}
