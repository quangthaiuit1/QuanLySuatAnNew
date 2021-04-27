package trong.lixco.com.jpa.entity;

import java.util.Base64;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "category_food")
public class CategoryFood extends AbstractEntity {
	private String name;
	private byte[] image;
	private String media_file_name;

	public String imageString() {
		try {
			return new String(Base64.getEncoder().encodeToString(image));
		} catch (Exception e) {
			return "";
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getMedia_file_name() {
		return media_file_name;
	}

	public void setMedia_file_name(String media_file_name) {
		this.media_file_name = media_file_name;
	}
	
	
}
