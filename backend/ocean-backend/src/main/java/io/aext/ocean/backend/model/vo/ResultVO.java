package io.aext.ocean.backend.model.vo;

import io.aext.ocean.backend.util.JsonHelper;
import lombok.Data;

/**
 * @author Rojar Smith
 *
 * @date 2021-06-30
 */
@Data
public class ResultVO<T> {
	String message;
	T data;

	public ResultVO(String msg) {
		// Must set null to avoid error:
		// No serializer found for class java.lang.Object
		this(msg, null);
	}

	public ResultVO(String msg, T data) {
		this.message = msg;
		this.data = data;
	}

	@Override
	public String toString() {
		return JsonHelper.stringify(this);
	}
}