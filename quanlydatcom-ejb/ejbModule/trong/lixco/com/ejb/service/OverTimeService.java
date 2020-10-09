package trong.lixco.com.ejb.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import trong.lixco.com.jpa.entity.OverTime;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class OverTimeService extends AbstractService<OverTime> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<OverTime> getEntityClass() {
		return OverTime.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	public List<OverTime> find(java.util.Date dateSearch, long shiftsId, String departmentCode) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OverTime> cq = cb.createQuery(OverTime.class);
		Root<OverTime> root = cq.from(OverTime.class);
		List<Predicate> queries = new ArrayList<>();
		if (dateSearch != null) {
			Predicate dateQuery = cb.equal(root.get("food_date"), dateSearch);
			queries.add(dateQuery);
		}
		if (shiftsId != 0) {
			Predicate shiftsQuery = cb.equal(root.get("shifts").get("id"), shiftsId);
			queries.add(shiftsQuery);
		}
		if (departmentCode != null) {
			Predicate shiftsQuery = cb.equal(root.get("department_code"), departmentCode);
			queries.add(shiftsQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<OverTime> query = em.createQuery(cq);
		List<OverTime> results = query.getResultList();
		if (!results.isEmpty()) {
			return results;
		} else {
			return new ArrayList<OverTime>();
		}
	}
}
