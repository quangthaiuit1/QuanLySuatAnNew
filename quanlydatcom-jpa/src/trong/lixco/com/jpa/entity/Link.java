package trong.lixco.com.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "link")
public class Link extends AbstractEntity {
	private String link_name;
	private String link_jdbc;
	
	public String getLink_name() {
		return link_name;
	}
	public void setLink_name(String link_name) {
		this.link_name = link_name;
	}
	public String getLink_jdbc() {
		return link_jdbc;
	}
	public void setLink_jdbc(String link_jdbc) {
		this.link_jdbc = link_jdbc;
	}
}
