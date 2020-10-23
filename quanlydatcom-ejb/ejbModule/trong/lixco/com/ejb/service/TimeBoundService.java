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
import trong.lixco.com.jpa.entity.TimeBound;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class TimeBoundService extends AbstractService<TimeBound> {
	@Inject
	private EntityManager em;
	@Resource
	private SessionContext ct;

	@Override
	protected Class<TimeBound> getEntityClass() {
		return TimeBound.class;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected SessionContext getUt() {
		return ct;
	}

	public TimeBound find(String name) {
		// primary
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TimeBound> cq = cb.createQuery(TimeBound.class);
		Root<TimeBound> root = cq.from(TimeBound.class);
		List<Predicate> queries = new ArrayList<>();
		if (name != null) {
			Predicate shiftsQuery = cb.equal(root.get("name"), name);
			queries.add(shiftsQuery);
		}

		Predicate data[] = new Predicate[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			data[i] = queries.get(i);
		}
		Predicate finalPredicate = cb.and(data);
		cq.where(finalPredicate);
		TypedQuery<TimeBound> query = em.createQuery(cq);
		List<TimeBound> results = query.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return new TimeBound();
		}
	}
}
