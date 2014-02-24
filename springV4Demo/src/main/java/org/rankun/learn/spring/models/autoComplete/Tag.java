package org.rankun.learn.spring.models.autoComplete;

public class Tag {

	public int id;
	public String tagName;

	// getter and setter methods

	public Tag(int id, String tagName) {
		this.id = id;
		this.tagName = tagName;
	}

    public int getId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }

}
