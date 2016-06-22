package com.socks.md.resolver;

import java.io.IOException;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

public class UploadUtil {

	private String mAccessKey;
	private String mSecertKey;
	private String mBucketName;
	private Auth mAuth;
	private UploadManager mUploadManager;

	public UploadUtil(String accessKey, String secertKEY, String bucketName) {
		mAccessKey = accessKey;
		mSecertKey = secertKEY;
		mBucketName = bucketName;
		mAuth = Auth.create(mAccessKey, mSecertKey);
		mUploadManager = new UploadManager();
	}

	private String getUpToken() {
		return mAuth.uploadToken(mBucketName);
	}

	public Response upload(String filePath, String fileName) throws IOException {
		try {
			return mUploadManager.put(filePath, fileName, getUpToken());
		} catch (QiniuException e) {
			return e.response;
		}
	}
}