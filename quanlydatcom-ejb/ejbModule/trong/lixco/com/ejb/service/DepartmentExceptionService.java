package trong.lixco.com.ejb.service;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import trong.lixco.com.jpa.entity.DepartException;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DepartmentExceptionService extends AbstractService<DepartException> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<DepartException> getEntityClass() {
		return DepartException.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

}
