package org.thermoweb.intellij.plugin.encrypt;

public enum Algorithm {
	PBE("PBEWithHmacSHA512AndAES_128");
	private final String code;

	Algorithm(final String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
