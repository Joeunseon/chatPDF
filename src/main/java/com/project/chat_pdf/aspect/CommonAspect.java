package com.project.chat_pdf.aspect;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class CommonAspect {

    /**
     * 로딩할 자바스크립트 명을 설정
     * @param joinPoint
     * @param viewName
     */
    @AfterReturning(value = "execution(public String com.project.chat_pdf..*Controller.*(..))", returning = "viewName")
    public void afterReturning(JoinPoint joinPoint, String viewName) {
        if ( viewName == null || StringUtils.isEmpty(viewName) )
            return; 

        ModelMap map = getModelMapFromArgs(joinPoint.getArgs());
        if ( map == null )
            return;

        String jsName = constructJsName(viewName);
        String jsObjName = constructJsObjName(viewName);

        map.put("jsName", jsName);
        map.put("jsObjName", jsObjName);

        log.info("jsName    " + jsName);
        log.info("jsObjName " + jsObjName);
    }

    /**
     * JoinPoint에서 ModelMap 객체를 추출
     * @param args
     * @return ModelMap
     */
    private ModelMap getModelMapFromArgs(Object[] args) {
        for ( Object obj : args ) {
            if ( obj instanceof ModelMap ) {
                return (ModelMap) obj;
            }
        }
        return null;
    }

    /**
     * viewName을 바탕으로 JavaScript 파일 경로 생성
     * @param viewName
     * @return JavaScript 파일 경로
     */
    private String constructJsName(String viewName) {
        if (!viewName.startsWith("/")) {
            viewName = "/" + viewName;
        }
        return "/js" + viewName + ".js";
    }

    /**
     * viewName을 바탕으로 JavaScript 객체 이름 생성
     * @param viewName
     * @return JavaScript 객체 이름
     */
    private String constructJsObjName(String viewName) {
        String jsObjName = viewName.contains("/") ?
                viewName.substring(viewName.lastIndexOf("/") + 1) :
                viewName;

        if (jsObjName != null && jsObjName.length() > 0)
            jsObjName = StringUtils.capitalize(jsObjName);
        
        return jsObjName;
    }
}
