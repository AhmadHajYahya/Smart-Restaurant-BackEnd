package com.smartRestaurant.general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;

public class FilePartToMultipartFileConverter {

	// Converts a reactive FilePart to a MultipartFile.
	@SuppressWarnings("deprecation")
	public static Mono<MultipartFile> convert(FilePart filePart) {
        return filePart.content()
            // Reduce the DataBuffers to a single DataBuffer by writing their contents to a ByteArrayOutputStream
            .reduce((dataBuffer1, dataBuffer2) -> {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    // Write the contents of the first DataBuffer to the outputStream
                    Channels.newChannel(outputStream).write(dataBuffer1.asByteBuffer().asReadOnlyBuffer());
                    // Write the contents of the second DataBuffer to the outputStream
                    Channels.newChannel(outputStream).write(dataBuffer2.asByteBuffer().asReadOnlyBuffer());
                } catch (IOException e) {
                    // Handle the IOException by throwing a RuntimeException
                    throw new RuntimeException(e);
                }
                // Return the second DataBuffer (the actual value returned doesn't matter for the reduce operation)
                return dataBuffer2;
            })
            // Map the resulting DataBuffer to a MockMultipartFile
            .map(dataBuffer -> {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    // Write the contents of the final DataBuffer to the outputStream
                    Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                } catch (IOException e) {
                    // Handle the IOException by throwing a RuntimeException
                    throw new RuntimeException(e);
                }
                // Create and return a MockMultipartFile with the file contents
                return new MockMultipartFile(filePart.filename(), filePart.filename(),
                    filePart.headers().getContentType().toString(), outputStream.toByteArray());
            });
    }
}
