/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.dts.springcloud;

import java.util.ArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import feign.RequestInterceptor;


/**
 * @author liushiming
 * @version SpringCloudContextConfig.java, v 0.0.1 2017年11月20日 下午2:34:06 liushiming
 */
@Configuration
public class SpringCloudContextConfig {

  @Configuration
  protected class FeignContextConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(new SpringCloudContextInterceptor())//
          .addPathPatterns("/*");
      super.addInterceptors(registry);
    }
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return new SpringCloudContextInterceptor();
  }

  @Bean
  public BeanPostProcessor contenxtInterceptorPostProcessor() {
    return new BeanPostProcessor() {

      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName)
          throws BeansException {
        return bean;
      }

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName)
          throws BeansException {
        if (bean instanceof RestTemplate) {
          RestTemplate restTemplate = (RestTemplate) bean;
          ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
          interceptors.add(new SpringCloudContextInterceptor());
          interceptors.addAll(restTemplate.getInterceptors());
          restTemplate.setInterceptors(interceptors);
        }
        return bean;
      }

    };
  }

}
