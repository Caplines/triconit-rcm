package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.UserInputQuestionDao;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.User;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionAnswer;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader;
import com.tricon.ruleengine.dao.OfficeDao;

@Repository
public class UserInputQuestionDaoImpl extends BaseDaoImpl implements UserInputQuestionDao {

	@Autowired
	OfficeDao OfficeDao;

	@Override
	public List<QuestionHeaderDto> getAllUserInputQuestions() {

		Session session = getSession();
		List<QuestionHeaderDto> dtoList = null;
		try {
			// Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(UserInputRuleQuestionHeader.class);
			criteria.add(Restrictions.eq("active", 1));
			criteria.addOrder(Order.asc("id"));
			criteria.addOrder(Order.asc("ruleName"));
			criteria.addOrder(Order.asc("questionOrder"));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("id"), "id");
			pjList.add(Projections.property("ruleName"), "ruleName");
			pjList.add(Projections.property("question"), "question");
			pjList.add(Projections.property("questionType"), "questionType");
			pjList.add(Projections.property("questionOrder"), "questionOrder");
			pjList.add(Projections.property("answerAppender"), "answerAppender");
			pjList.add(Projections.property("answerAppenderPosition"), "answerAppenderPosition");
			pjList.add(Projections.property("hardCodedAnswer"), "hardCodedAnswer");
			pjList.add(Projections.property("canChangeAnswer"), "canChangeAnswer");

			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(QuestionHeaderDto.class));
			dtoList = criteria.list();
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return dtoList;

	}

	@Override
	public List<UserInputRuleQuestionHeader> getAllUserInputQuestionsDbModel() {

		Session session = getSession();
		List<UserInputRuleQuestionHeader> dtoList = null;
		try {
			// Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(UserInputRuleQuestionHeader.class);
			criteria.add(Restrictions.eq("active", 1));
			criteria.addOrder(Order.asc("id"));
			criteria.addOrder(Order.asc("ruleName"));
			criteria.addOrder(Order.asc("questionOrder"));
			dtoList = criteria.list();
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return dtoList;

	}

	@Override
	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto) {
		Session session = getSession();
		List<QuestionAnswerDto> dtoList = null;
		try {
			// Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(UserInputRuleQuestionAnswer.class);
			criteria.add(Restrictions.eq("tpId", dto.getTreatmentPlanId()));

			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", dto.getOfficeId()));

			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("id"), "answerId");
			pjList.add(Projections.property("tpId"), "tpId");
			pjList.add(Projections.property("userInputRuleQuestionHeader.id"), "questionId");
			pjList.add(Projections.property("ivfId"), "ivfId");
			pjList.add(Projections.property("patId"), "patId");
			pjList.add(Projections.property("office.uuid"), "officeId");
			pjList.add(Projections.property("answer"), "answer");

			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(QuestionAnswerDto.class));
			dtoList = criteria.list();
			// transaction.commit();
		} finally {
			closeSession(session);

		}
		return dtoList;
	}

	@Override
	public UserInputRuleQuestionAnswer getUserAnswersByQuestionIdServiceCode(UserInputDto dto, int questionId,
			String serviceCode) {
		Session session = getSession();
		UserInputRuleQuestionAnswer dtoA = null;
		try {
			Transaction transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(UserInputRuleQuestionAnswer.class);
			criteria.add(Restrictions.eq("tpId", dto.getTreatmentPlanId()));
			criteria.add(Restrictions.eqOrIsNull("serviceCode", serviceCode));
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", dto.getOfficeId()));
			criteria.add(Restrictions.eq("userInputRuleQuestionHeader.id", questionId));

			dtoA = (UserInputRuleQuestionAnswer) criteria.uniqueResult();
			transaction.commit();
		} finally {
			closeSession(session);

		}
		return dtoA;
	}

	private List<UserInputRuleQuestionAnswer> getAnswersByAnswerId(Integer[] answerIds) {
		Session session = getSession();
		List<UserInputRuleQuestionAnswer> dtoA = null;
		try {
			Criteria criteria = session.createCriteria(UserInputRuleQuestionAnswer.class);
			criteria.add(Restrictions.in("id", answerIds));
			dtoA = criteria.list();
		} finally {
			closeSession(session);

		}
		return dtoA;
	}

	/*
	 * Not used for now... (non-Javadoc)
	 * 
	 * @see com.tricon.ruleengine.dao.UserInputQuestionDao#saveAndUpdateAnswers(com.
	 * tricon.ruleengine.dto.QuestionAnswerDto,
	 * com.tricon.ruleengine.model.db.Office,
	 * com.tricon.ruleengine.model.db.UserInputRuleQuestionHeader,
	 * com.tricon.ruleengine.model.db.User, java.lang.String)
	 */
	@Override
	public Serializable saveAndUpdateAnswers(QuestionAnswerDto dto, Office off,
			UserInputRuleQuestionHeader userInputRuleQuestionHeader, User user, String serviceCode) {
		// get Already Persisted Data
		UserInputDto d = new UserInputDto();
		d.setOfficeId(dto.getOfficeId());
		d.setTreatmentPlanId(dto.getTpId());
		UserInputRuleQuestionAnswer ans = getUserAnswersByQuestionIdServiceCode(d, userInputRuleQuestionHeader.getId(),
				serviceCode);
		if (ans == null) {
			UserInputRuleQuestionAnswer a = new UserInputRuleQuestionAnswer();
			a.setAnswer(dto.getAnswer());
			a.setIvfId(dto.getIvfId());
			a.setOffice(off);
			a.setPatId(dto.getPatId());
			a.setTpId(dto.getTpId());
			a.setServiceCode(serviceCode);
			a.setCreatedBy(user);

			a.setUserInputRuleQuestionHeader(userInputRuleQuestionHeader);
			return saveEntiy(a);

		} else {
			// update answer
			ans.setAnswer(dto.getAnswer());
			ans.setUpdatedBy(user);
			ans.setUpdatedDate(new Date());

			updateEntity(ans);
			return ans.getId();

		}

	}

	@Override
	public void saveUserAnswers(Integer[] ids, Map<Integer, UserAnswerDto> map) {
		if (ids != null && ids.length > 0) {
			List<UserInputRuleQuestionAnswer> li = getAnswersByAnswerId(ids);
			for (UserInputRuleQuestionAnswer ans : li) {
				UserAnswerDto data = map.get(ans.getId());
				ans.setAnswer(data.getAnswer());
				updateEntity(ans);
			}

		}

	}

}
