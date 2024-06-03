package com.example.webserver.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PlacePageableMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PlacePageable.class)
                && PageableWrapper.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
       String sort = webRequest.getParameter("sort");
       String page = webRequest.getParameter("page");
       String size = webRequest.getParameter("size");

       if (sort == null || sort.isEmpty()) sort = "placeName";;
       if (page == null || page.isEmpty()) page = "0";
       if (size == null || size.isEmpty()) size = "10";

       if (sort.equals("placeName")) return new PageableWrapper(sort, PageRequest.of(Integer.parseInt(page),
               Integer.parseInt(size),
               Sort.by(Sort.Direction.ASC, sort)));

       return new PageableWrapper(sort, PageRequest.of(Integer.parseInt(page),
               Integer.parseInt(size),
               Sort.by(Sort.Direction.DESC, sort)));
    }
}
