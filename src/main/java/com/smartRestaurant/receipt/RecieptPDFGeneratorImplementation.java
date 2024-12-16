package com.smartRestaurant.receipt;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.smartRestaurant.GCS.ReceiptsGCSSaver;
import com.smartRestaurant.general.MsgCreator;
import com.smartRestaurant.meal.Meal;
import com.smartRestaurant.mealOrder.MealOrderRepository;
import com.smartRestaurant.user.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RecieptPDFGeneratorImplementation implements RecieptPDFGeneratorService {
	private final ReceiptsGCSSaver rSaver;
	private final MealOrderRepository mealOrderRepository;
	private final UserRepository userRepository;

	public RecieptPDFGeneratorImplementation(MealOrderRepository mealOrderRepository, UserRepository userRepository,
			ReceiptsGCSSaver rSaver) {
		super();
		this.mealOrderRepository = mealOrderRepository;
		this.userRepository = userRepository;
		this.rSaver = rSaver;
	}

	@Override
	public Mono<String> generatePdf(String mealOrderId, String receiptId, Double totalPrice, Flux<Meal> meals) {
		return this.mealOrderRepository.findById(mealOrderId)
				.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("Meal order"))))
				.flatMap(mealOrder -> this.userRepository.findById(mealOrder.getUserId())
						.switchIfEmpty(Mono.error(new IllegalArgumentException(MsgCreator.notFound("User"))))
						.flatMap(user -> meals.collectList() // Collect Flux<Meal> into List<Meal>
								.flatMap(mealList -> {
									return createPdf(receiptId, mealOrder.getmOrderID(), user.getName(),
											user.getPhoneNumber(), totalPrice, mealList)
											.flatMap(document -> rSaver.saveReceipt(document, mealOrder.getmOrderID(),
													user.getName()));
								})));
	}

	private Mono<byte[]> createPdf(String receiptId, String orderId, String customerName, String customerPhoneNumber,
			Double tPrice, List<Meal> meals) {
		return Mono.fromSupplier(() -> {
			String font = FontFactory.COURIER;
			String symbol = "NIS";
			String header = "Smart Restaurant";
			String dateOfCreation = "Date: " + LocalDate.now().toString();
			String cName = "Customer: " + customerName;
			String customerPhone = "Phone: +972" + customerPhoneNumber;
			String receiptNumber = "Receipt No: " + receiptId;
			String orderNumber = "Order No: " + orderId;
			String tPriceText = "Total Price: " + tPrice + " " + symbol;
			String businessContact = "Contact Us: 123-456-7890 | info@smartrestaurant.com";

			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				Document document = new Document();
				PdfWriter.getInstance(document, baos);

				document.open();

				// Header
				Chunk cHeader = new Chunk(header, FontFactory.getFont(font, 24, BaseColor.BLACK));
				Paragraph pHeader = new Paragraph(cHeader);
				pHeader.setAlignment(Element.ALIGN_CENTER);
				document.add(pHeader);

				// Line Separator
				LineSeparator separator = new LineSeparator();
				separator.setLineColor(BaseColor.BLACK);
				document.add(new Chunk(separator));

				// Customer and Date Section
				PdfPTable infoTable = new PdfPTable(2);
				infoTable.setWidthPercentage(100);

				// Add customer details
				PdfPCell customerCell = new PdfPCell();
				customerCell.setBorder(PdfPCell.NO_BORDER);
				customerCell.addElement(new Phrase(cName, FontFactory.getFont(font, 16, BaseColor.BLACK)));
				customerCell.addElement(new Phrase(customerPhone, FontFactory.getFont(font, 16, BaseColor.BLACK)));
				infoTable.addCell(customerCell);

				// Add date
				PdfPCell dateCell = new PdfPCell(
						new Phrase(dateOfCreation, FontFactory.getFont(font, 16, BaseColor.BLACK)));
				dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				dateCell.setVerticalAlignment(Element.ALIGN_TOP);
				dateCell.setBorder(PdfPCell.NO_BORDER);
				infoTable.addCell(dateCell);

				document.add(infoTable);

				// Receipt Number
				Paragraph receiptParagraph = new Paragraph(receiptNumber,
						FontFactory.getFont(font, 16, BaseColor.BLACK));
				receiptParagraph.setAlignment(Element.ALIGN_CENTER);
				receiptParagraph.setSpacingBefore(40); // Add spacing before the receipt number
				document.add(receiptParagraph);

				// Order Number
				Paragraph orderParagraph = new Paragraph(orderNumber, FontFactory.getFont(font, 16, BaseColor.BLACK));
				orderParagraph.setAlignment(Element.ALIGN_CENTER);
				orderParagraph.setSpacingBefore(10); // Add a small spacing before the order number
				document.add(orderParagraph);

				// Table with 2 columns and 4 rows
				PdfPTable table = new PdfPTable(2);
				table.setWidthPercentage(100);
				table.setSpacingBefore(20); // Add spacing before the table

				// Set column widths (first column 50%, second column 50%)
				float[] columnWidths = { 2f, 1f };
				table.setWidths(columnWidths);

				// First row with a background color and updated headers
				BaseColor headerColor = new BaseColor(230, 230, 250); // Light lavender color
				PdfPCell cell1 = new PdfPCell(new Phrase("Details", FontFactory.getFont(font, 16, BaseColor.BLACK)));
				cell1.setBackgroundColor(headerColor);
				table.addCell(cell1);

				PdfPCell cell2 = new PdfPCell(new Phrase("Price", FontFactory.getFont(font, 16, BaseColor.BLACK)));
				cell2.setBackgroundColor(headerColor);
				table.addCell(cell2);

				// Other rows without background color
				for (Meal meal : meals) {
					table.addCell(new Phrase(meal.getTitle(), FontFactory.getFont(font, 14, BaseColor.BLACK)));
					table.addCell(
							new Phrase(meal.getPrice() + " " + symbol, FontFactory.getFont(font, 14, BaseColor.BLACK))); // Example
																															// price
																															// values
				}
				document.add(table);

				// Total price
				Paragraph totalPrice = new Paragraph(tPriceText, FontFactory.getFont(font, 16, BaseColor.BLACK));
				totalPrice.setSpacingBefore(40); // Add spacing before the total price
				totalPrice.setAlignment(Element.ALIGN_RIGHT);
				document.add(totalPrice);

				// Calculate available space and add a spacer
				float availableHeight = document.getPageSize().getHeight() - document.topMargin()
						- document.bottomMargin() - pHeader.getTotalLeading() - infoTable.getTotalHeight()
						- receiptParagraph.getTotalLeading() - orderParagraph.getTotalLeading() - table.getTotalHeight()
						- totalPrice.getTotalLeading();

				PdfPTable spacer = new PdfPTable(1);
				spacer.setWidthPercentage(100);
				PdfPCell spacerCell = new PdfPCell(new Phrase(" "));
				spacerCell.setBorder(PdfPCell.NO_BORDER);
				spacerCell.setFixedHeight(document.getPageSize().getHeight() - availableHeight);
				spacer.addCell(spacerCell);
				document.add(spacer);

				// Business Contact Details at the bottom
				PdfPTable contactTable = new PdfPTable(1);
				contactTable.setWidthPercentage(100);
				PdfPCell contactCell = new PdfPCell(
						new Phrase(businessContact, FontFactory.getFont(font, 12, BaseColor.BLACK)));
				contactCell.setBorder(PdfPCell.NO_BORDER);
				contactCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				contactTable.addCell(contactCell);
				document.add(contactTable);

				document.close();

				return baos.toByteArray();

			} catch (Exception e) {
				e.printStackTrace();
				return new byte[0]; // Return an empty byte array in case of error
			}
		});
	}
}
