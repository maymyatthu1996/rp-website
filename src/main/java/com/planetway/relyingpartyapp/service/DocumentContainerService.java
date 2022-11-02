package com.planetway.relyingpartyapp.service;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.planetway.relyingpartyapp.config.AppProperties;
import com.planetway.relyingpartyapp.model.RpFile;
import com.planetway.relyingpartyapp.xml.sign.FileToSignDocument;

@Service
public class DocumentContainerService {

	private static final JAXBContext jaxbContext = createJaxbContext(FileToSignDocument.class);
	private final FileService fileService;
	private final AppProperties appProperties;

	public DocumentContainerService(FileService fileService, AppProperties appProperties) {
		this.fileService = fileService;
		this.appProperties = appProperties;
	}

	public String createFileSignContainer(MultipartFile multipartFile) {
		RpFile rpFile = fileService.storeFile(multipartFile);
		return createContainer(rpFile);
	}

	public String createFileSignContainer() {
		RpFile rpFile = fileService.storeFile("RP file".getBytes(), "rp-file.txt", "text/plain");
		return createContainer(rpFile);
	}

	private String createContainer(RpFile rpFile) {
		FileToSignDocument fileToSignDocument = new FileToSignDocument();
		fileToSignDocument.setSignRequestType("file_sign");
		fileToSignDocument.setFileURI(appProperties.getBaseUrl() + "/files/" + rpFile.getFileId());
		fileToSignDocument.setHashToSign(rpFile.getHash());
		fileToSignDocument.setData(FileToSignDocument.RequestData.builder().fileName(rpFile.getFileName()).build());
		return marshalDocument(fileToSignDocument);
	}

	private String marshalDocument(FileToSignDocument document) {
		StringWriter stringWriter = new StringWriter();
		try {
			XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(stringWriter);
			xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
			xmlStreamWriter.writeCharacters("\n");

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(document, stringWriter);
		} catch (XMLStreamException | JAXBException e) {
			throw new RuntimeException(e);
		}
		return stringWriter.toString();
	}

	private static JAXBContext createJaxbContext(Class... contextClasses) {
		try {
			return JAXBContext.newInstance(contextClasses);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
