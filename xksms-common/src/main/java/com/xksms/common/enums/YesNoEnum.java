package com.xksms.common.enums;

import com.xksms.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum YesNoEnum implements IntArrayValuable {
	YES(1, "是"),
	NO(0, "否");

	private final int code;
	private final String desc;

	public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(YesNoEnum::getCode).toArray();


	@Override
	public int[] array() {
		return ARRAYS;
	}
}