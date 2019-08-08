package com.lib.sdk.struct;

/**
 * 非鱼眼,但是需要矫正的镜头
 *
 */
public class SDK_FishEyeFrameCM extends SDK_FishEyeFrame {
	public byte st_0_camera;	// 镜头类型(内部定义序号),从0开始
	public byte st_1_resv[] = new byte[7];	// 预留
	
	@Override
	public boolean equals(Object o) {
		if ( null == o ) {
			return false;
		}
		
		if ( !(o instanceof SDK_FishEyeFrameCM) ) {
			return false;
		}
		
		SDK_FishEyeFrameCM cp = (SDK_FishEyeFrameCM)o;
		
		return (this.st_0_camera == cp.st_0_camera);
	}
}
