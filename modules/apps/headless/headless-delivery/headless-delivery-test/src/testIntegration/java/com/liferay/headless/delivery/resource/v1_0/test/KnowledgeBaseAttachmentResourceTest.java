/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.delivery.client.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.resource.v1_0.KnowledgeBaseAttachmentResource;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class KnowledgeBaseAttachmentResourceTest
	extends BaseKnowledgeBaseAttachmentResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_kbArticle = _addKBArticle();
	}

	@Override
	@Test
	public void testDeleteKnowledgeBaseAttachment() throws Exception {
		super.testDeleteKnowledgeBaseAttachment();

		// Knowledge base attachment without permission

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			_addRestrictedKnowledgeBaseAttachment();

		KnowledgeBaseAttachmentResource
			userWithoutPermissionsKnowledgeBaseAttachmentResource =
				_getUserWithoutPermissionsKnowledgeBaseAttachmentResource();

		assertHttpResponseStatusCode(
			403,
			userWithoutPermissionsKnowledgeBaseAttachmentResource.
				deleteKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));

		// File entry that is not a knowledge base attachment

		FileEntry fileEntry = _addFileEntry();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteKnowledgeBaseAttachmentHttpResponse(
					fileEntry.getFileEntryId()));

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntry(fileEntry.getFileEntryId()));
	}

	@Override
	@Test
	public void testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode();

		// Nonexistent knowledge base article

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					knowledgeBaseAttachment.getExternalReferenceCode()));

		// Nonexistent knowledge base attachment

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Knowledge base attachment associated to a different article

		KBArticle prevKBArticle = _kbArticle;

		_kbArticle = _addKBArticle();

		KnowledgeBaseAttachment newKnowledgeBaseAttachment =
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					prevKBArticle.getExternalReferenceCode(),
					newKnowledgeBaseAttachment.getExternalReferenceCode()));

		// Knowledge base attachment without update permission

		KBArticle viewableKBArticle = _addViewableKBArticle();

		KnowledgeBaseAttachment viewableKnowledgeBaseAttachment =
			_addKnowledgeBaseAttachment(viewableKBArticle);

		KnowledgeBaseAttachmentResource
			siteMemberKnowledgeBaseAttachmentResource =
				_getSiteMemberKnowledgeBaseAttachmentResource();

		assertHttpResponseStatusCode(
			403,
			siteMemberKnowledgeBaseAttachmentResource.
				deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					viewableKBArticle.getGroupId(),
					viewableKBArticle.getExternalReferenceCode(),
					viewableKnowledgeBaseAttachment.
						getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					viewableKnowledgeBaseAttachment.getId()));
	}

	@Override
	@Test
	public void testGetKnowledgeBaseAttachment() throws Exception {
		super.testGetKnowledgeBaseAttachment();

		// Knowledge base attachment without permission

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			_addRestrictedKnowledgeBaseAttachment();

		KnowledgeBaseAttachmentResource
			userWithoutPermissionsKnowledgeBaseAttachmentResource =
				_getUserWithoutPermissionsKnowledgeBaseAttachmentResource();

		assertHttpResponseStatusCode(
			404,
			userWithoutPermissionsKnowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					knowledgeBaseAttachment.getId()));

		// Knowledge base attachment on an unapproved knowledge base article

		FileEntry draftFileEntry = _addDraftKBArticleAttachment();

		assertHttpResponseStatusCode(
			200,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					draftFileEntry.getFileEntryId()));

		// File entry in a folder named after a knowledge base article

		FileEntry fileEntry = _addFileEntry(
			String.valueOf(_kbArticle.getResourcePrimKey()));

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getKnowledgeBaseAttachmentHttpResponse(
					fileEntry.getFileEntryId()));
	}

	@Override
	@Test
	public void testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode();

		// Nonexistent knowledge base article

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					knowledgeBaseAttachment.getExternalReferenceCode()));

		// Nonexistent knowledge base attachment

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Knowledge base attachment associated to a different article

		KBArticle prevKBArticle = _kbArticle;

		_kbArticle = _addKBArticle();

		KnowledgeBaseAttachment newKnowledgeBaseAttachment =
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment();

		assertHttpResponseStatusCode(
			404,
			knowledgeBaseAttachmentResource.
				getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCodeHttpResponse(
					testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId(),
					prevKBArticle.getExternalReferenceCode(),
					newKnowledgeBaseAttachment.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage()
		throws Exception {

		super.testGraphQLGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage();
	}

	@Override
	protected void assertValid(
			KnowledgeBaseAttachment knowledgeBaseAttachment,
			Map<String, File> multipartFiles)
		throws Exception {

		Assert.assertEquals(
			new String(FileUtil.getBytes(multipartFiles.get("file"))),
			_read(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					knowledgeBaseAttachment.getContentUrl()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"title"};
	}

	@Override
	protected Map<String, File> getMultipartFiles() {
		return HashMapBuilder.<String, File>put(
			"file",
			() -> {
				File file = new File(_tempFileName);

				FileUtil.write(file, TestDataConstants.TEST_BYTE_ARRAY);

				return file;
			}
		).build();
	}

	@Override
	protected KnowledgeBaseAttachment randomKnowledgeBaseAttachment()
		throws Exception {

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			super.randomKnowledgeBaseAttachment();

		_tempFileName = FileUtil.createTempFileName();

		File file = new File(_tempFileName);

		knowledgeBaseAttachment.setTitle(file.getName());

		return knowledgeBaseAttachment;
	}

	@Override
	protected KnowledgeBaseAttachment
			testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected String
			testDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getExpectedActions(
				Long knowledgeBaseArticleId)
		throws Exception {

		return Collections.emptyMap();
	}

	@Override
	protected Long
		testGetKnowledgeBaseArticleKnowledgeBaseAttachmentsPage_getKnowledgeBaseArticleId() {

		return _kbArticle.getResourcePrimKey();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGetKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	@Override
	protected String
			testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected Long
		testGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getSiteId() {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testGraphQLDeleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected String
			testGraphQLGetSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode_getKnowledgeBaseArticleExternalReferenceCode()
		throws Exception {

		return _kbArticle.getExternalReferenceCode();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGraphQLKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return testDeleteKnowledgeBaseAttachment_addKnowledgeBaseAttachment();
	}

	@Override
	protected KnowledgeBaseAttachment
			testGraphQLSiteKnowledgeBaseAttachment_addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment();
	}

	private KBArticle _addDraftKBArticle() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(testGroup.getGroupId());
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return _addKBArticle(serviceContext, TestPropsValues.getUserId());
	}

	private FileEntry _addDraftKBArticleAttachment() throws Exception {
		KBArticle kbArticle = _addDraftKBArticle();

		return _kbArticleLocalService.addAttachment(
			TestPropsValues.getUserId(), kbArticle.getResourcePrimKey(),
			RandomTestUtil.randomString() + ".txt",
			new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			ContentTypes.TEXT_PLAIN);
	}

	private FileEntry _addFileEntry() throws Exception {
		return _addFileEntry(RandomTestUtil.randomString());
	}

	private FileEntry _addFileEntry(String folderName) throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, folderName,
			RandomTestUtil.randomString(), serviceContext);

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			folder.getFolderId(), RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(), null, null,
			null, TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);
	}

	private KBArticle _addKBArticle() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		return _addKBArticle(
			serviceContext,
			_userLocalService.getGuestUserId(testGroup.getCompanyId()));
	}

	private KBArticle _addKBArticle(ServiceContext serviceContext, long userId)
		throws Exception {

		return _kbArticleLocalService.addKBArticle(
			null, userId, PortalUtil.getClassNameId(KBFolder.class.getName()),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, RandomTestUtil.nextDate(), null, null, null, serviceContext);
	}

	private KnowledgeBaseAttachment _addKnowledgeBaseAttachment()
		throws Exception {

		return _addKnowledgeBaseAttachment(_kbArticle);
	}

	private KnowledgeBaseAttachment _addKnowledgeBaseAttachment(
			KBArticle kbArticle)
		throws Exception {

		return knowledgeBaseAttachmentResource.
			postKnowledgeBaseArticleKnowledgeBaseAttachment(
				kbArticle.getResourcePrimKey(), randomKnowledgeBaseAttachment(),
				getMultipartFiles());
	}

	private KnowledgeBaseAttachment _addRestrictedKnowledgeBaseAttachment()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		return _addKnowledgeBaseAttachment(
			_addKBArticle(serviceContext, TestPropsValues.getUserId()));
	}

	private KBArticle _addViewableKBArticle() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(testGroup.getGroupId());

		return _addKBArticle(serviceContext, TestPropsValues.getUserId());
	}

	private KnowledgeBaseAttachmentResource _getKnowledgeBaseAttachmentResource(
		String password, User user) {

		return KnowledgeBaseAttachmentResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private KnowledgeBaseAttachmentResource
			_getSiteMemberKnowledgeBaseAttachmentResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_users.add(user);

		_userLocalService.addGroupUsers(
			testGroup.getGroupId(), new long[] {user.getUserId()});

		return _getKnowledgeBaseAttachmentResource(password, user);
	}

	private KnowledgeBaseAttachmentResource
			_getUserWithoutPermissionsKnowledgeBaseAttachmentResource()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_users.add(user);

		return _getKnowledgeBaseAttachmentResource(password, user);
	}

	private String _read(String url) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(url);
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private KBArticle _kbArticle;

	@Inject
	private KBArticleLocalService _kbArticleLocalService;

	private String _tempFileName;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}