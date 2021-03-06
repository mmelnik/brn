package com.epam.brn.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.epam.brn.config.AwsConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AwsCloudServiceTest {
    @InjectMocks
    lateinit var awsCloudService: AwsCloudService
    @Mock
    lateinit var awsConfig: AwsConfig

    @Test
    fun `should get correct signature for client upload`() {
        // GIVEN
        Mockito.`when`(awsConfig.secretAccessKey).thenReturn("99999999999999999999999999999")
        Mockito.`when`(awsConfig.region).thenReturn("us-east-2")
        Mockito.`when`(awsConfig.serviceName).thenReturn("s3")
        Mockito.`when`(awsConfig.bucketLink).thenReturn("http://somebucket.s3.amazonaws.com")

        var conditions: AwsConfig.Conditions = Mockito.mock(AwsConfig.Conditions::class.java)
        Mockito.`when`(conditions.date).thenReturn("20200130")
        Mockito.`when`(conditions.bucket).thenReturn("bucket" to "somebucket")
        Mockito.`when`(conditions.acl).thenReturn("acl" to "private")
        Mockito.`when`(conditions.uuid).thenReturn("x-amz-meta-uuid" to "c49791b2-b27b-4edf-bac8-8734164c20e6")
        Mockito.`when`(conditions.serverSideEncryption).thenReturn("x-amz-server-side-encryption" to "AES256")
        Mockito.`when`(conditions.credential).thenReturn("x-amz-credential" to "AKIAI7KLKATWVCMEKGPA/20200130/us-east-2/s3/aws4_request")
        Mockito.`when`(conditions.algorithm).thenReturn("x-amz-algorithm" to "AWS4-HMAC-SHA256")
        Mockito.`when`(conditions.dateTime).thenReturn("x-amz-date" to "20200130T113917Z")
        Mockito.`when`(conditions.expiration).thenReturn("expiration" to "2020-01-30T21:39:17.114Z")
        Mockito.`when`(conditions.uploadKey).thenReturn("key" to "tasks/\${filename}")
        Mockito.`when`(conditions.successActionRedirect).thenReturn("success_action_redirect" to "")
        Mockito.`when`(conditions.contentTypeStartsWith).thenReturn("Content-Type" to "")
        Mockito.`when`(conditions.metaTagStartsWith).thenReturn("x-amz-meta-tag" to "")
        Mockito.`when`(awsConfig.getConditions(anyString())).thenReturn(conditions)
        // WHEN
        val signature = awsCloudService.uploadForm("")
        // THEN
        val signatureExpected: Map<String, Any> = mapOf(
            "action" to "http://somebucket.s3.amazonaws.com",
            "input" to listOf(
                mapOf("policy" to "ew0KICAiY29uZGl0aW9ucyIgOiBbIHsNCiAgICAiYnVja2V0IiA6ICJzb21lYnVja2V0Ig0KICB9LCB7DQogICAgImFjbCIgOiAicHJpdmF0ZSINCiAgfSwgWyAic3RhcnRzLXdpdGgiLCAiJGtleSIsICJ0YXNrcy8ke2ZpbGVuYW1lfSIgXSwgew0KICAgICJ4LWFtei1tZXRhLXV1aWQiIDogImM0OTc5MWIyLWIyN2ItNGVkZi1iYWM4LTg3MzQxNjRjMjBlNiINCiAgfSwgew0KICAgICJ4LWFtei1zZXJ2ZXItc2lkZS1lbmNyeXB0aW9uIiA6ICJBRVMyNTYiDQogIH0sIHsNCiAgICAieC1hbXotY3JlZGVudGlhbCIgOiAiQUtJQUk3S0xLQVRXVkNNRUtHUEEvMjAyMDAxMzAvdXMtZWFzdC0yL3MzL2F3czRfcmVxdWVzdCINCiAgfSwgew0KICAgICJ4LWFtei1hbGdvcml0aG0iIDogIkFXUzQtSE1BQy1TSEEyNTYiDQogIH0sIHsNCiAgICAieC1hbXotZGF0ZSIgOiAiMjAyMDAxMzBUMTEzOTE3WiINCiAgfSBdLA0KICAiZXhwaXJhdGlvbiIgOiAiMjAyMC0wMS0zMFQyMTozOToxNy4xMTRaIg0KfQ=="),
                mapOf("x-amz-signature" to "4d39e2b2ac5833352544d379dadad1ffba3148d9936d814f36f50b7af2cd8e8e"),
                mapOf("key" to "tasks/\${filename}"),
                mapOf("acl" to "private"),
                mapOf("x-amz-meta-uuid" to "c49791b2-b27b-4edf-bac8-8734164c20e6"),
                mapOf("x-amz-server-side-encryption" to "AES256"),
                mapOf("x-amz-credential" to "AKIAI7KLKATWVCMEKGPA/20200130/us-east-2/s3/aws4_request"),
                mapOf("x-amz-algorithm" to "AWS4-HMAC-SHA256"),
                mapOf("x-amz-date" to "20200130T113917Z")
            )
        )
        Assertions.assertEquals(signatureExpected, signature)
    }

    @Test
    fun `should convert to base64 string`() {
        // GIVEN
        var conditions = hashMapOf(
            "expiration" to "2020-01-30T21:39:17.114Z",
            "conditions" to
                    listOf(
                        hashMapOf("bucket" to "somebucket"),
                        hashMapOf("acl" to "private"),
                        arrayOf("starts-with", "\$key", "tasks/\${filename}"),
                        hashMapOf("x-amz-meta-uuid" to "c49791b2-b27b-4edf-bac8-8734164c20e6"),
                        hashMapOf("x-amz-server-side-encryption" to "AES256"),
                        hashMapOf("x-amz-credential" to "AKIAI7KLKATWVCMEKGPA/20200130/us-east-2/s3/aws4_request"),
                        hashMapOf("x-amz-algorithm" to "AWS4-HMAC-SHA256"),
                        hashMapOf("x-amz-date" to "20200130T113917Z")
                    )
        )
        // WHEN
        val base64 = awsCloudService.toJsonBase64(conditions)
        // THEN
        val expected = "ew0KICAiY29uZGl0aW9ucyIgOiBbIHsNCiAgICAiYnVja2V0IiA6ICJzb21lYnVja2V0Ig0KICB9LCB7DQogICAgImFjbCIgOiAicHJpdmF0ZSINCiAgfSwgWyAic3RhcnRzLXdpdGgiLCAiJGtleSIsICJ0YXNrcy8ke2ZpbGVuYW1lfSIgXSwgew0KICAgICJ4LWFtei1tZXRhLXV1aWQiIDogImM0OTc5MWIyLWIyN2ItNGVkZi1iYWM4LTg3MzQxNjRjMjBlNiINCiAgfSwgew0KICAgICJ4LWFtei1zZXJ2ZXItc2lkZS1lbmNyeXB0aW9uIiA6ICJBRVMyNTYiDQogIH0sIHsNCiAgICAieC1hbXotY3JlZGVudGlhbCIgOiAiQUtJQUk3S0xLQVRXVkNNRUtHUEEvMjAyMDAxMzAvdXMtZWFzdC0yL3MzL2F3czRfcmVxdWVzdCINCiAgfSwgew0KICAgICJ4LWFtei1hbGdvcml0aG0iIDogIkFXUzQtSE1BQy1TSEEyNTYiDQogIH0sIHsNCiAgICAieC1hbXotZGF0ZSIgOiAiMjAyMDAxMzBUMTEzOTE3WiINCiAgfSBdLA0KICAiZXhwaXJhdGlvbiIgOiAiMjAyMC0wMS0zMFQyMTozOToxNy4xMTRaIg0KfQ=="
        Assertions.assertEquals(expected, base64)
    }

    @Test
    fun `should get folder list without pagination`() {
        // GIVEN
        val mockS3: AmazonS3 = Mockito.mock(AmazonS3::class.java)

        val result: ListObjectsV2Result = listObjectsV2Result(listOf("file", "folder/", "folder/file", "folder/folder/"))
        Mockito.`when`(result.isTruncated).thenReturn(false)

        Mockito.`when`(awsConfig.amazonS3).thenReturn(mockS3)
        Mockito.`when`(awsConfig.bucketName).thenReturn("test")
        Mockito.`when`(mockS3.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(result)
        // WHEN
        val listBucket = awsCloudService.listBucket()
        // THEN
        val expected: List<String> = listOf("folder/", "folder/folder/")
        Assertions.assertEquals(expected, listBucket)
    }

    @Test
    fun `should get folder list with pagination`() {
        // GIVEN
        val mockS3: AmazonS3 = Mockito.mock(AmazonS3::class.java)

        val result: ListObjectsV2Result = listObjectsV2Result(listOf("file", "folder/", "folder/file", "folder/folder/"))
        Mockito.`when`(result.isTruncated).thenReturn(true)
        Mockito.`when`(result.nextContinuationToken).thenReturn("asd")

        val result2: ListObjectsV2Result = listObjectsV2Result(listOf("file3", "folder3/", "folder3/file3", "folder3/folder3/"))
        Mockito.`when`(result2.isTruncated).thenReturn(false)

        Mockito.`when`(awsConfig.amazonS3).thenReturn(mockS3)
        Mockito.`when`(awsConfig.bucketName).thenReturn("test")
        Mockito.`when`(mockS3.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(result, result2)
        // WHEN
        val listBucket = awsCloudService.listBucket()
        // THEN
        val expected: List<String> = listOf("folder/", "folder/folder/", "folder3/", "folder3/folder3/")
        Assertions.assertEquals(expected, listBucket)
    }

    private fun listObjectsV2Result(keys: List<String>): ListObjectsV2Result {
        val result: ListObjectsV2Result = Mockito.mock(ListObjectsV2Result::class.java)
        val objectSummaries: List<S3ObjectSummary> =
            getObjectSummaries(keys)
        Mockito.`when`(result.objectSummaries).thenReturn(objectSummaries)
        return result
    }

    private fun getObjectSummaries(keys: List<String>): List<S3ObjectSummary> {
        val objectSummaries: ArrayList<S3ObjectSummary> = ArrayList()
        keys.forEach {
            val os = S3ObjectSummary()
            os.key = it
            objectSummaries.add(os)
        }
        return objectSummaries
    }
}
