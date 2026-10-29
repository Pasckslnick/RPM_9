package com.example.newsservice.controller;

import com.example.newsservice.model.NewsModel;
import com.example.newsservice.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NewsApiControllerTest {
    private NewsService service;
    private NewsApiController controller;

    @BeforeEach
    void setUp() {
        service = mock(NewsService.class);
        controller = new NewsApiController(service);
    }

    @Test
    void getAllDelegatesToService() {
        List<NewsModel> expected = List.of(new NewsModel());
        when(service.getAll()).thenReturn(expected);
        assertSame(expected, controller.getAll());
    }

    @Test
    void searchPassesKeywordToService() {
        controller.search("cars");
        verify(service).search("cars");
    }

    @Test
    void deleteDelegatesToService() {
        Authentication admin = mock(Authentication.class);
        List<org.springframework.security.core.GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(admin).getAuthorities();
        controller.delete(4, admin);
        verify(service).delete(4);
    }

    @Test
    void customerCannotDeleteNews() {
        Authentication customer = mock(Authentication.class);
        List<org.springframework.security.core.GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        doReturn(authorities).when(customer).getAuthorities();

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> controller.delete(4, customer));
        verifyNoInteractions(service);
    }
}
