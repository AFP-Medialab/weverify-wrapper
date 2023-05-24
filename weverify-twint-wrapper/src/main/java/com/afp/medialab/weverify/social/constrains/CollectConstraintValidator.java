package com.afp.medialab.weverify.social.constrains;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.afp.medialab.weverify.social.model.CollectRequest;

public class CollectConstraintValidator implements ConstraintValidator<CollectConstraint, CollectRequest> {

	@Override
	public void initialize(CollectConstraint constraintAnnotation) {

	}

	@Override
	public boolean isValid(CollectRequest collectRequest, ConstraintValidatorContext context) {
		if ((collectRequest.getKeywordList() == null) && collectRequest.getUserList() == null
				&& collectRequest.getKeywordAnyList() == null)
			return false;
		else if (collectRequest.getKeywordList() != null && collectRequest.getKeywordList().isEmpty()
				&& collectRequest.getUserList() != null && collectRequest.getUserList().isEmpty()
				&& collectRequest.getKeywordAnyList() != null && collectRequest.getKeywordAnyList().isEmpty())
			return false;
		else
			return true;
	}

}
