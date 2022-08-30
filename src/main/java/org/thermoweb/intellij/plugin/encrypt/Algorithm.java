package org.thermoweb.intellij.plugin.encrypt;

public enum Algorithm {
	PBE("PBEWithHmacSHA512AndAES_128"),
	MD5("PBEWithMD5AndDES");
	private final String code;

	Algorithm(final String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
