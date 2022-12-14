package com.melody.melody.adapter.security;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler{
    private final ObjectProvider<MusicSecurityExpression> musicSecurityExpressions;
    private final ObjectProvider<UserSecurityExpression> userSecurityExpressions;
    private final ObjectProvider<PostSecurityExpression> postSecurityExpressions;

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(Authentication auth, MethodInvocation mi) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(auth);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());

        StandardEvaluationContext sec = super.createEvaluationContextInternal(auth, mi);
        sec.setRootObject(root);

        MusicSecurityExpression musicSecurityExpression = musicSecurityExpressions.getObject();
        musicSecurityExpression.setAuthentication(auth);
        UserSecurityExpression userSecurityExpression = userSecurityExpressions.getObject();
        userSecurityExpression.setAuthentication(auth);
        PostSecurityExpression postSecurityExpression = postSecurityExpressions.getObject();
        postSecurityExpression.setAuthentication(auth);

        sec.setVariable("music", musicSecurityExpression);
        sec.setVariable("user", userSecurityExpression);
        sec.setVariable("post", postSecurityExpression);

        return sec;
    }
}