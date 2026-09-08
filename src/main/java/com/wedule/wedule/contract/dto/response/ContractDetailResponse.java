package com.wedule.wedule.contract.dto.response;

import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractStyle;
import com.wedule.wedule.member.entity.Member;
import com.wedule.wedule.reservation.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;

// 계약서 화면(프론트)을 그리는 데 필요한 모든 정보를 한 번에 담은 응답
public class ContractDetailResponse {

    // 계약서 자체 정보
    private Long contractId;
    private ContractStyle style;
    private String content;

    // 업체(작가) 정보
    private String businessName;
    private String signatureImageBase64; // 사인 이미지를 화면에 바로 쓸 수 있는 문자열 형태로 변환해서 내려줌

    // 예약(고객) 정보
    private String groomName;
    private String brideName;
    private String phone;
    private LocalDate weddingDate;
    private LocalTime weddingTime;
    private String venueName;
    private String packageName;
    private int packagePrice;

    private List<OptionLine> options;
    private List<PaymentLine> payments;

    public ContractDetailResponse(Contract contract, Reservation reservation, Member member,
                                  List<OptionLine> options, List<PaymentLine> payments) {
        this.contractId = contract.getId();
        this.style = contract.getStyle();
        this.content = contract.getContent();

        this.businessName = member.getBusinessName();
        this.signatureImageBase64 = member.getSignatureImage() != null
                ? Base64.getEncoder().encodeToString(member.getSignatureImage())
                : null;

        this.groomName = reservation.getGroomName();
        this.brideName = reservation.getBrideName();
        this.phone = reservation.getPhone();
        this.weddingDate = reservation.getWeddingDate();
        this.weddingTime = reservation.getWeddingTime();
        this.venueName = reservation.getVenueName();
        this.packageName = reservation.getPackageInfo().getName();
        this.packagePrice = reservation.getPackageInfo().getPrice();

        this.options = options;
        this.payments = payments;
    }

    public Long getContractId() { return contractId; }
    public ContractStyle getStyle() { return style; }
    public String getContent() { return content; }
    public String getBusinessName() { return businessName; }
    public String getSignatureImageBase64() { return signatureImageBase64; }
    public String getGroomName() { return groomName; }
    public String getBrideName() { return brideName; }
    public String getPhone() { return phone; }
    public LocalDate getWeddingDate() { return weddingDate; }
    public LocalTime getWeddingTime() { return weddingTime; }
    public String getVenueName() { return venueName; }
    public String getPackageName() { return packageName; }
    public int getPackagePrice() { return packagePrice; }
    public List<OptionLine> getOptions() { return options; }
    public List<PaymentLine> getPayments() { return payments; }

    // 옵션 한 줄 (이름 + 금액)
    public static class OptionLine {
        private String name;
        private int price;

        public OptionLine(String name, int price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public int getPrice() { return price; }
    }

    // 결제 한 줄 (종류 + 금액 + 입금여부)
    public static class PaymentLine {
        private String type;
        private int amount;
        private boolean isPaid;

        public PaymentLine(String type, int amount, boolean isPaid) {
            this.type = type;
            this.amount = amount;
            this.isPaid = isPaid;
        }

        public String getType() { return type; }
        public int getAmount() { return amount; }
        public boolean isPaid() { return isPaid; }
    }
}