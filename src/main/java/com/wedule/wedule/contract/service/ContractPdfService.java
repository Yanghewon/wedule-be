package com.wedule.wedule.contract.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wedule.wedule.contract.entity.Contract;
import com.wedule.wedule.contract.entity.ContractStyle;
import com.wedule.wedule.member.Member;
import com.wedule.wedule.option.entity.Option;
import com.wedule.wedule.payment.entity.Payment;
import com.wedule.wedule.payment.repository.PaymentRepository;
import com.wedule.wedule.reservation.entity.Reservation;
import com.wedule.wedule.reservation.entity.ReservationOption;
import com.wedule.wedule.reservation.repository.ReservationOptionRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// Contract 정보를 실제 PDF 바이트로 조립하는 서비스
// DB에 PDF 파일 자체를 저장하지 않고, 다운로드 요청이 올 때마다 최신 데이터로 새로 생성함
@Service
public class ContractPdfService {

    private final ReservationOptionRepository reservationOptionRepository;
    private final PaymentRepository paymentRepository;

    public ContractPdfService(ReservationOptionRepository reservationOptionRepository,
                              PaymentRepository paymentRepository) {
        this.reservationOptionRepository = reservationOptionRepository;
        this.paymentRepository = paymentRepository;
    }

    public byte[] generate(Contract contract) throws IOException, DocumentException {
        Reservation reservation = contract.getReservation();
        Member member = reservation.getMember();
        Color accentColor = accentColorOf(contract.getStyle());

        // 1. 한글이 깨지지 않도록, 프로젝트에 넣어둔 폰트 파일을 읽어서 PDF 전용 폰트 객체로 만듦
        BaseFont baseFont = loadKoreanFont();
        Font titleFont = new Font(baseFont, 20, Font.BOLD, accentColor);
        Font headingFont = new Font(baseFont, 13, Font.BOLD, accentColor);
        Font bodyFont = new Font(baseFont, 10, Font.NORMAL, Color.DARK_GRAY);
        Font labelFont = new Font(baseFont, 10, Font.BOLD, Color.DARK_GRAY);

        // 2. 빈 PDF 문서와, 그 문서를 실제로 그려 넣을 writer를 준비
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // 3. 제목
        Paragraph title = new Paragraph("웨딩 스냅 촬영 계약서", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 4. 업체(작가) 정보
        document.add(sectionHeading("촬영 업체 정보", headingFont));
        document.add(infoTable(labelFont, bodyFont, accentColor,
                new String[]{"업체명", member.getBusinessName()},
                new String[]{"연락처", member.getPhone()},
                new String[]{"이메일", member.getEmail()}
        ));

        // 5. 예약(고객) 정보
        document.add(sectionHeading("예약 정보", headingFont));
        document.add(infoTable(labelFont, bodyFont, accentColor,
                new String[]{"신랑 / 신부", reservation.getGroomName() + " / " + reservation.getBrideName()},
                new String[]{"연락처", reservation.getPhone()},
                new String[]{"예식 일시", formatDateTime(reservation)},
                new String[]{"예식 장소", reservation.getVenueName()},
                new String[]{"촬영 패키지", reservation.getPackageInfo().getName()
                        + " (" + formatAmount(reservation.getPackageInfo().getPrice()) + "원)"}
        ));

        // 6. 옵션 목록
        List<Option> options = reservationOptionRepository.findByReservationId(reservation.getId()).stream()
                .map(ReservationOption::getOption)
                .collect(Collectors.toList());
        if (!options.isEmpty()) {
            document.add(sectionHeading("추가 옵션", headingFont));
            document.add(optionTable(options, labelFont, bodyFont, accentColor));
        }

        // 7. 결제 내역
        List<Payment> payments = paymentRepository.findByReservationId(reservation.getId());
        if (!payments.isEmpty()) {
            document.add(sectionHeading("결제 내역", headingFont));
            document.add(paymentTable(payments, labelFont, bodyFont, accentColor));
        }

        // 8. 계약 조항 (작가가 작성/수정한 본문)
        document.add(sectionHeading("계약 조항", headingFont));
        Paragraph contentParagraph = new Paragraph(contract.getContent(), bodyFont);
        contentParagraph.setSpacingAfter(30);
        document.add(contentParagraph);

        // 9. 계약자 서명 (작가 측 + 신부 측)
        document.add(sectionHeading("계약자 서명", headingFont));

        // 작가(업체) 서명 - 프로필에 등록해둔 사인 이미지를 그대로 사용
        Paragraph businessLabel = new Paragraph("촬영 업체", labelFont);
        businessLabel.setSpacingBefore(5);
        document.add(businessLabel);

        if (member.getSignatureImage() != null) {
            Image signature = Image.getInstance(member.getSignatureImage());
            signature.scaleToFit(120, 60);
            document.add(signature);
        }
        document.add(new Paragraph(member.getBusinessName() + " (인)", bodyFont));

        // 신부(고객) 서명 - 아직 시스템에 신부의 사인 이미지를 받는 기능이 없어서,
        // 실제 종이/PDF에 손으로 서명할 수 있도록 빈 서명란만 비워둠
        Paragraph customerLabel = new Paragraph("예약자", labelFont);
        customerLabel.setSpacingBefore(15);
        document.add(customerLabel);

        Paragraph customerSignLine = new Paragraph("\n\n_______________________", bodyFont);
        document.add(customerSignLine);
        document.add(new Paragraph(reservation.getBrideName() + " (인)", bodyFont));

        document.close();
        return outputStream.toByteArray();
    }

    // 스타일별 강조색
    private Color accentColorOf(ContractStyle style) {
        return switch (style) {
            case CLASSIC -> new Color(80, 60, 40);      // 진한 브라운
            case MODERN -> new Color(40, 60, 100);      // 네이비
            case MINIMAL -> new Color(60, 60, 60);      // 차분한 그레이
            case ELEGANT -> new Color(150, 60, 90);     // 로즈
            case WARM -> new Color(180, 110, 50);       // 테라코타
        };
    }

    // 프로젝트에 포함시킨 나눔고딕 폰트를 읽어서, 한글이 깨지지 않는 PDF 전용 폰트로 변환
    private BaseFont loadKoreanFont() throws IOException, DocumentException {
        ClassPathResource resource = new ClassPathResource("fonts/NanumGothic.ttf");
        byte[] fontBytes = resource.getInputStream().readAllBytes();
        return BaseFont.createFont("NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
    }

    private Paragraph sectionHeading(String text, Font font) {
        Paragraph heading = new Paragraph(text, font);
        heading.setSpacingBefore(15);
        heading.setSpacingAfter(8);
        return heading;
    }

    // 라벨-값 쌍을 세로로 나열한 2열 표 (업체 정보, 예약 정보에서 사용)
    private PdfPTable infoTable(Font labelFont, Font bodyFont, Color accentColor, String[]... rows) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1f, 3f});
        } catch (DocumentException e) {
            // 너비 비율 설정 실패는 무시하고 기본값으로 진행
        }

        for (String[] row : rows) {
            PdfPCell labelCell = new PdfPCell(new Phrase(row[0], labelFont));
            labelCell.setBorder(Rectangle.BOTTOM);
            labelCell.setBorderColor(accentColor);
            labelCell.setPadding(6);

            PdfPCell valueCell = new PdfPCell(new Phrase(row[1] != null ? row[1] : "-", bodyFont));
            valueCell.setBorder(Rectangle.BOTTOM);
            valueCell.setBorderColor(new Color(220, 220, 220));
            valueCell.setPadding(6);

            table.addCell(labelCell);
            table.addCell(valueCell);
        }
        return table;
    }

    // 옵션 목록을 "옵션명 | 금액" 2열 표로 그려주는 메서드
    private PdfPTable optionTable(List<Option> options, Font labelFont, Font bodyFont, Color accentColor) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        PdfPCell nameHeader = new PdfPCell(new Phrase("옵션명", labelFont));
        PdfPCell priceHeader = new PdfPCell(new Phrase("금액", labelFont));
        nameHeader.setBackgroundColor(accentColor);
        priceHeader.setBackgroundColor(accentColor);
        nameHeader.setPadding(6);
        priceHeader.setPadding(6);
        table.addCell(nameHeader);
        table.addCell(priceHeader);

        for (Option option : options) {
            table.addCell(new PdfPCell(new Phrase(option.getName(), bodyFont)));
            String sign = option.getPrice() >= 0 ? "+" : "";
            PdfPCell priceCell = new PdfPCell(new Phrase(sign + formatAmount(option.getPrice()) + "원", bodyFont));
            table.addCell(priceCell);
        }
        return table;
    }

    // 결제 내역을 "종류 | 금액 | 입금여부" 3열 표로 그려주는 메서드
    private PdfPTable paymentTable(List<Payment> payments, Font labelFont, Font bodyFont, Color accentColor) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        for (String header : new String[]{"종류", "금액", "입금여부"}) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, labelFont));
            headerCell.setBackgroundColor(accentColor);
            headerCell.setPadding(6);
            table.addCell(headerCell);
        }

        for (Payment payment : payments) {
            table.addCell(new PdfPCell(new Phrase(payment.getType().name(), bodyFont)));
            table.addCell(new PdfPCell(new Phrase(formatAmount(payment.getAmount()) + "원", bodyFont)));
            table.addCell(new PdfPCell(new Phrase(payment.isPaid() ? "입금완료" : "미입금", bodyFont)));
        }
        return table;
    }

    // 예식 날짜와 시간을 "2026-08-09 11:00" 형태의 한 문자열로 합쳐주는 메서드
    private String formatDateTime(Reservation reservation) {
        return reservation.getWeddingDate() + " "
                + reservation.getWeddingTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // 금액 숫자에 천 단위 콤마를 넣어주는 메서드 (예: 1500000 -> "1,500,000")
    private String formatAmount(int amount) {
        return String.format("%,d", amount);
    }
}