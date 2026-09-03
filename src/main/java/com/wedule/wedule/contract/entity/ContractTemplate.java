package com.wedule.wedule.contract.entity;

import com.wedule.wedule.member.Member;
import jakarta.persistence.*;

// 작가별로 하나씩 갖는 계약서 기본 템플릿(조항 내용)
// 실제 계약서(Contract) 생성 시 이 내용을 불러와서 기본값으로 쓰고, 필요하면 수정함
@Entity
@Table(name = "contract_template")
public class ContractTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    protected ContractTemplate() {
    }

    public ContractTemplate(Member member, String content) {
        this.member = member;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getContent() {
        return content;
    }
}