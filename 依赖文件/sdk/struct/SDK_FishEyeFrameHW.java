package com.lib.sdk.struct;

/**
 * 硬校正时使用的信息帧内容
 * @author Administrator
 *
 */
public class SDK_FishEyeFrameHW extends SDK_FishEyeFrame {
	public byte st_0_secene;	// 参考SDK_FishEyeParam.FISHEYE_SECENE_E定义, 
								// 目前只有SDK_FISHEYE_SECENE_P360_FE需要校正
	
	public byte st_1_resv[] = new byte[3];	// 预留
	
	@Override
	public boolean equals(Object o) {
		if ( null == o ) {
			return false;
		}
		
		if ( !(o instanceof SDK_FishEyeFrameHW) ) {
			return false;
		}
		
		SDK_FishEyeFrameHW cp = (SDK_FishEyeFrameHW)o;
		
		return (this.st_0_secene == cp.st_0_secene);
	}
}
