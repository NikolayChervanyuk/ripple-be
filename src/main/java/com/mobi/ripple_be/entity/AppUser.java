package com.mobi.ripple_be.entity;

import com.mobi.ripple_be.util.AppUserDetails;
import com.mobi.ripple_be.util.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AppUser extends BaseEntity implements AppUserDetails {

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, unique = true, length = 40)
    private String username;

    @Column(length = 100)
    private String fullName;

    @Column(nullable = false, length = 60, columnDefinition = "CHAR(60)")
    private String password;

    @Column(length = 250)
    private String bio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Long followers;

    @Column(nullable = false)
    private Long following;

    @Column(nullable = false)
    private Long postsCount;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private Instant lastActive = Instant.now();

    @Column(nullable = false)
    private Instant lastIssuedTokenRevocation;

//    @OneToMany(mappedBy = "author")
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "author")
//    private List<PostComment> postComments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "author")
//    private List<CommentReply> commentReplies = new ArrayList<>();
//    @ManyToMany
//    @JoinTable(
//            name = "users_liked_posts",
//            joinColumns = @JoinColumn(name = "user_id", nullable = false),
//            inverseJoinColumns = @JoinColumn(name = "post_id", nullable = false)
//    )
//    private Post likedPosts;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return AppUserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return AppUserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return AppUserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
