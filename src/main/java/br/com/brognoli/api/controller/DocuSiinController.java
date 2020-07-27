package br.com.brognoli.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.Signer;
import com.migcomponents.migbase64.Base64;


@CrossOrigin
@RestController
@RequestMapping("/docusign")
public class DocuSiinController {
	
	private static final String IntegratorKey = "fc32f5b3-2ecb-4bd8-acec-3f140bde2b54";
	private static final String UserId = "89da2d40-17a9-4981-86c6-91a1d1ef908e";
	private static final String privateKeyFullPath = "c:\\logs\\docusign_private_key.txt";
	//private static final byte[] privateKeyBytes = Base64.decode(System.getenv("4a2a6499-7596-44d2-aa1f-2aa850e2e91b"));
	
	
	private static final String Recipient = "jizidoro@globo.com";
	private static final String SignTest1File = "c:\\logs\\boletos.pdf";
	private static final String BaseUrl = "https://demo.docusign.net";
	
	@GetMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	private String testeDoc( ) {
		byte[] fileBytes = null;
		try {
			// String currentDir = new java.io.File(".").getCononicalPath();

			String currentDir = System.getProperty("user.dir");

			Path path = Paths.get(SignTest1File);
			fileBytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create an envelope to be signed
				EnvelopeDefinition envDef = new EnvelopeDefinition();
				envDef.setEmailSubject("Teste de documento");
				envDef.setEmailBlurb("Segue documento para teste de aplicação");

				// add a document to the envelope
				Document doc = new Document();
				String base64Doc = Base64.encodeToString(fileBytes, false);
				doc.setDocumentBase64(base64Doc);
				doc.setName("TesteArquivo.pdf");
				doc.setDocumentId("1");

				List<Document> docs = new ArrayList<Document>();
				docs.add(doc);
				envDef.setDocuments(docs);

				// Add a recipient to sign the document
				Signer signer = new Signer();
				signer.setEmail(Recipient);
				signer.setName("Julio Izidoro");
				signer.setRecipientId("1");
				
				// Above causes issue
				envDef.setRecipients(new Recipients());
				envDef.getRecipients().setSigners(new ArrayList<Signer>());
				envDef.getRecipients().getSigners().add(signer);

				// send the envelope (otherwise it will be "created" in the Draft folder
				envDef.setStatus("sent");

				ApiClient apiClient = new ApiClient(BaseUrl);
				// String currentDir = System.getProperty("user.dir");

				try {
					// IMPORTANT NOTE:
					// the first time you ask for a JWT access token, you should grant access by
					// making the following call
					// get DocuSign OAuth authorization url:
					// String oauthLoginUrl = apiClient.getJWTUri(IntegratorKey, RedirectURI,
					// OAuthBaseUrl);
					// open DocuSign OAuth authorization url in the browser, login and grant access
					// Desktop.getDesktop().browse(URI.create(oauthLoginUrl));
					// END OF NOTE

					byte[] privateKeyBytes = null;
					try {
						privateKeyBytes = Files.readAllBytes(Paths.get(privateKeyFullPath));
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (privateKeyBytes == null)
						return "erro";

					java.util.List<String> scopes = new ArrayList<String>();
					scopes.add(OAuth.Scope_SIGNATURE);
					scopes.add(OAuth.Scope_IMPERSONATION);

					OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(IntegratorKey, UserId, scopes, privateKeyBytes,
							3600);
					// now that the API client has an OAuth token, let's use it in all
					// DocuSign APIs
					apiClient.setAccessToken(oAuthToken.getAccessToken(), oAuthToken.getExpiresIn());
					UserInfo userInfo = apiClient.getUserInfo(oAuthToken.getAccessToken());

					System.out.println("UserInfo: " + userInfo);
					// parse first account's baseUrl
					// below code required for production, no effect in demo (same
					// domain)
					apiClient.setBasePath(userInfo.getAccounts().get(0).getBaseUri() + "/restapi");
					Configuration.setDefaultApiClient(apiClient);
					String accountId = userInfo.getAccounts().get(0).getAccountId();

					EnvelopesApi envelopesApi = new EnvelopesApi();

					EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);

					System.out.println("EnvelopeSummary: " + envelopeSummary);

				} catch (ApiException ex) {
					System.out.println("Exception: " + ex);
					return "erro";
				} catch (Exception e) {
					System.out.println("Exception: " + e.getLocalizedMessage());
					return "erro";
				}

				
				
				

				return "ok";

	}


}
