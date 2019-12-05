package com.tricon.ruleengine.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.UserInputQuestionDao;
import com.tricon.ruleengine.dto.QuestionAnswerDto;
import com.tricon.ruleengine.dto.QuestionHeaderDto;
import com.tricon.ruleengine.dto.UserAnswerDto;
import com.tricon.ruleengine.dto.UserInputDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.UserInputRuleQuestionAnswer;
import com.tricon.ruleengine.service.UserInputService;

/**
 * 
 * @author Deepak.Dogra
 *
 */
@Service
public class UserInputServiceImpl implements UserInputService {

	@Autowired
	UserInputQuestionDao uiqDao;

	@Override
	public List<QuestionHeaderDto> getUserInput() {
		// TODO Auto-generated method stub
		return uiqDao.getAllUserInputQuestions();
	}
	
	@Override
	public List<QuestionAnswerDto> getUserAnswers(UserInputDto dto){
		
		return uiqDao.getUserAnswers(dto);
	}

	@Override
	public void saveUserAnswers(List<UserAnswerDto> userAnswerDtoList) {
	
		if (userAnswerDtoList !=null && userAnswerDtoList.size()>0) {
			Integer [] ids= new Integer[userAnswerDtoList.size()];
			
			Map<Integer,UserAnswerDto> map=new HashMap<>();
			List<Integer> idsL= new ArrayList<>();
			String patId="";
			String treatmentPlanId="";
			String officeId="";
			
			
			for(UserAnswerDto d:userAnswerDtoList) {
				if (d.getAnswerId()==-1000) {
					if (d.getAnswer().split("-%%-").length==3) { 
					patId=d.getAnswer().split("-%%-")[0];
					treatmentPlanId=d.getAnswer().split("-%%-")[1];
					officeId=d.getAnswer().split("-%%-")[2];
					}
					continue;
				}
				idsL.add(d.getAnswerId());
				map.put(d.getAnswerId(), d);
			}
			if (!officeId.equals("")) {
			 ids=idsL.toArray(ids);
			 uiqDao.saveUserAnswers(ids,map);
			 //update permanent status
			 UserInputDto dt=new UserInputDto();
			 dt.setOfficeId(officeId);
             dt.setTreatmentPlanId(treatmentPlanId);			 
             uiqDao.updateUserAnswersPremanent(officeId, patId, treatmentPlanId);
			}
		}
		
		
		
		
	}

	@Override
	public List<QuestionAnswerDto> getUserAnswers(String patId, String ivfId,String TRAN_DATE, Office office) {
		// TODO Auto-generated method stub
		return uiqDao.getUserAnswersByPatIvfAndOff(patId,ivfId,TRAN_DATE,office);
	}

	@Override
	public List<QuestionAnswerDto> getUserAnswersPermanent(UserInputDto dto) {
		
		 return uiqDao.getUserAnswersPermanent(dto);
	}

	@Override
	public List<QuestionAnswerDto> getUserAnswersPermanent(String patId, String ivfId, String TRAN_DATE,
			Office office) {
		// TODO Auto-generated method stub
		return uiqDao.getUserAnswersByPatIvfAndOffPermanent(patId,ivfId,TRAN_DATE,office);
	}
	
	

}
