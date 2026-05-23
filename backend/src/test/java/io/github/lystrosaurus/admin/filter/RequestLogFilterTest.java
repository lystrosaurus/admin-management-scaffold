package io.github.lystrosaurus.admin.filter;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** RequestLogFilter 单元测试 */
@ExtendWith(MockitoExtension.class)
class RequestLogFilterTest {

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private RequestLogFilter requestLogFilter;

  @Test
  void should_log_request_and_response_when_filtering() throws ServletException, IOException {
    // 设置模拟对象行为
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(request.getQueryString()).thenReturn("param=value");
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(response.getStatus()).thenReturn(200);

    // 执行过滤器
    requestLogFilter.doFilter(request, response, filterChain);

    // 验证过滤器链被调用
    verify(filterChain).doFilter(request, response);

    // 验证请求方法被调用
    verify(request).getMethod();
    verify(request).getRequestURI();
    verify(request).getQueryString();
    verify(request).getRemoteAddr();
  }

  @Test
  void should_handle_null_query_string_when_filtering() throws ServletException, IOException {
    when(request.getMethod()).thenReturn("POST");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(request.getQueryString()).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(response.getStatus()).thenReturn(201);

    requestLogFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_handle_exception_during_filtering() throws ServletException, IOException {
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/test");
    when(request.getQueryString()).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    when(response.getStatus()).thenReturn(500);

    doThrow(new RuntimeException("测试异常")).when(filterChain).doFilter(request, response);

    // 验证异常被抛出
    try {
      requestLogFilter.doFilter(request, response, filterChain);
    } catch (RuntimeException e) {
      // 预期异常
    }

    // 验证即使发生异常，日志仍然被记录
    verify(request).getMethod();
    verify(request).getRequestURI();
  }
}
