package com.lib.sdk.struct;

public class SDK_FishEyeParam {
	public int st_0_appType = -1; // 如枚举FISHEYE_APP_TYPE_E
	public int st_1_secene = -1; // 如枚举FISHEYE_SECENE_E
	public int st_2_duty = -1;// 灯泡亮度 0 - 100;
	public int st_3_reserv[] = new int[7];

	public interface FISHEYE_APP_TYPE_E {
		/**
		 * 天花板
		 */
		public static final int SDK_FISHEYE_APP_CEIL = 0;
		/**
		 * 桌上
		 */
		public static final int SDK_FISHEYE_APP_TABL = 1;
		/**
		 * 墙上
		 */
		public static final int SDK_FISHEYE_APP_WALL = 2;

	}

	public interface FISHEYE_SECENE_E {
		public static final int SDK_FISHEYE_SECENE_ORIG = 0;
		public static final int SDK_FISHEYE_SECENE_R = 1;
		public static final int SDK_FISHEYE_SECENE_P180_ALL = 2;
		public static final int SDK_FISHEYE_SECENE_P180_ONE = 3;
		public static final int SDK_FISHEYE_SECENE_P180_TWO = 4;
		public static final int SDK_FISHEYE_SECENE_P360_FULL = 5;
		public static final int SDK_FISHEYE_SECENE_P360_SEPE = 6;
		public static final int SDK_FISHEYE_SECENE_P360_HALF = 7;
		public static final int SDK_FISHEYE_SECENE_ROP_R = 8;
		public static final int SDK_FISHEYE_SECENE_RORR_R = 9;
		public static final int SDK_FISHEYE_SECENE_RRRR_R = 10;
		public static final int SDK_FISHEYE_SECENE_RRP_R = 11;
		public static final int SDK_FISHEYE_SECENE_P360_FE = 12;
	}

	@Override
	public String toString() {
		return "SDK_FishEyeParam [st_0_appType=" + st_0_appType
				+ ", st_1_secene=" + st_1_secene + "]";
	}

}
