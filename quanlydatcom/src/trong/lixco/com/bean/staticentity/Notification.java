package trong.lixco.com.bean.staticentity;

import javax.faces.application.FacesMessage;

import org.primefaces.PrimeFaces;

public class Notification {
	static public void NOTI_SUCCESS(String mess) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Th�ng b�o", mess);
		PrimeFaces.current().dialog().showMessageDynamic(message);
	}
	static public void NOTI_WARN(String mess) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Th�ng b�o", mess);
		PrimeFaces.current().dialog().showMessageDynamic(message);
	}
	static public void NOTI_ERROR(String mess) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Th�ng b�o", mess);
		PrimeFaces.current().dialog().showMessageDynamic(message);
	}
}
