package com.lib.sdk.struct;

/**
 * 软校正信息帧内容
 * @author Administrator
 *
 */
public class SDK_FishEyeFrameSW extends SDK_FishEyeFrame {
	public byte st_0_version;				//结构体的版本号(如果修改下面的成员导致app需要对新老程序分开处理时，则需要让扩展部将参数设置工具中的版本号加1，并修改设备上的默认配置)
	public byte st_1_lensType;				//镜头类型，如枚举E_FISH_LENS_TYPE
	public short st_2_centerOffsetX;		//圆心偏差横坐标  单位:像素点
	public short st_3_centerOffsetY;		//圆心偏差纵坐标  单位:像素点
	public short st_4_radius;				//半径  单位:像素点
	public short st_5_imageWidth;			//圆心校正时的图像宽度  单位:像素点
	public short st_6_imageHeight;			//圆心校正时的图像高度  单位:像素点
	public byte st_7_viewAngle;				//视角  0:俯视   1:平视
	public byte st_8_viewMode;				//显示模式   0:360VR
	public byte st_9_resv[] = new byte[10];	// 预留
	
	public interface FISHEYE_LENS_TYPE_E {
		public static final int SDK_FISHEYE_LENS_GENERAL = 0;
		public static final int SDK_FISHEYE_LENS_360VR = 1;
		public static final int SDK_FISHEYE_LENS_360LVR = 2;
		public static final int SDK_FISHEYE_LENS_180VR = 3;
		public static final int SDK_FISHEYE_LENS_DUAL_360VR = 4;
		public static final int SDK_FISHEYE_LENS_DUAL_180VR = 5;
	}

	@Override
	public boolean equals(Object o) {
		if ( null == o ) {
			return false;
		}
		
		if ( !(o instanceof SDK_FishEyeFrameSW) ) {
			return false;
		}
		
		SDK_FishEyeFrameSW cp = (SDK_FishEyeFrameSW)o;
		
		return (this.st_0_version == cp.st_0_version
				&& this.st_1_lensType == cp.st_1_lensType
				&& this.st_2_centerOffsetX == cp.st_2_centerOffsetX
				&& this.st_3_centerOffsetY == cp.st_3_centerOffsetY
				&& this.st_4_radius == cp.st_4_radius
				&& this.st_5_imageWidth == cp.st_5_imageWidth
				&& this.st_6_imageHeight == cp.st_6_imageHeight
				&& this.st_7_viewAngle == cp.st_7_viewAngle
				&& this.st_8_viewMode == cp.st_8_viewMode);
	}
	
	
}
