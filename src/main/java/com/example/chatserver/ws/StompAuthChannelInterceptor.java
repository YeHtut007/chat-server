package com.example.chatserver.ws;

import com.example.chatserver.security.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {
  private final JwtService jwt;

  public StompAuthChannelInterceptor(JwtService jwt) { this.jwt = jwt; }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {
    	  String auth = acc.getFirstNativeHeader("Authorization");
    	  if (auth == null || !auth.startsWith("Bearer ")) {
    	    throw new IllegalArgumentException("Missing Authorization header");
    	  }
    	  String token = auth.substring(7);
    	  if (!jwt.isValid(token)) {
    	    throw new IllegalArgumentException("Invalid JWT");
    	  }
    	  String username = jwt.extractUsername(token);
    	  var authn = new UsernamePasswordAuthenticationToken(
    	      username, null, List.of(new SimpleGrantedAuthority("USER")));
    	  acc.setUser(authn);
    	}

    return message;
  }
}
