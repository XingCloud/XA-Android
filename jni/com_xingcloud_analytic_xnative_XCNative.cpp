//
//  com_xingcloud_analytic_xnative_XCNative.cpp
//  demo
//
//  Created by he song on 11-7-22.
//  Copyright 2011年 __MyCompanyName__. All rights reserved.
//

#include "com_xingcloud_analytic_xnative_XCNative.h"
#include <string>
#include <android/log.h>
#include "curl.h"
#include <stdlib.h>
#include <stdio.h>

#include <sys/types.h>
#include "malloc.h"

//#include "xmalloc.h"
//#define ISXDIGIT(x) (isxdigit((int) ((unsigned char)x)))
const char *baseUrl="http://analytic.337.com/";
//50.22.226.197  http://analytic.337.com/  analytic.337.com
const char *normalParam = "index.php?";
const char *customParam = "storelog.php?";
const char *pageUrl = "http://analytic.337.com/index.php?";
//http://analytic.337.com/index.php?appid=""&uid=""&event=page.visit&json_var={current_page:param1,time_duration:param2,next_page:param3}


static void *xmalloc_fatal(size_t size) {
  if (size==0) return NULL;
  fprintf(stderr, "Out of memory.");
  exit(1);
}

void *xmalloc (size_t size) {
  void *ptr = malloc (size);
  if (ptr == NULL) return xmalloc_fatal(size);
  return ptr;
}

void *xcalloc (size_t nmemb, size_t size) {
  void *ptr = calloc (nmemb, size);
  if (ptr == NULL) return xmalloc_fatal(nmemb*size);
  return ptr;
}

void *xrealloc (void *ptr, size_t size) {
  void *p = realloc ((char*)ptr, size);
  if (p == NULL) return xmalloc_fatal(size);
  return p;
}

char *xstrdup (const char *s) {
  void *ptr = xmalloc(strlen(s)+1);
  strcpy ((char*)ptr, s);
  return (char*) ptr;
}
struct MemoryStruct {
  char *data;
  size_t size; //< bytes remaining (r), bytes accumulated (w)

  size_t start_size; //< only used with ..AndCall()
  void (*callback)(void*,int,size_t,size_t); //< only used with ..AndCall()
  void *callback_data; //< only used with ..AndCall()
};
//
static size_t
WriteMemoryCallback(void *ptr, size_t size, size_t nmemb, void *data) {
  size_t realsize = size * nmemb;
  struct MemoryStruct *mem = (struct MemoryStruct *)data;

  mem->data = (char *)realloc(mem->data, mem->size + realsize + 1);
  if (mem->data) {
    memcpy(&(mem->data[mem->size]), ptr, realsize);
    mem->size += realsize;
    mem->data[mem->size] = 0;
  }
  return realsize;
}


char *php_url_encode(char const *s, int len, int *new_length)
{
    #define safe_emalloc(nmemb, size, offset)    malloc((nmemb) * (size) + (offset))
    static unsigned char hexchars[] = "0123456789ABCDEF";
    register unsigned char c;
    unsigned char *to, *start;
    unsigned char const *from, *end;

    from = (unsigned char *)s;
    end = (unsigned char *)s + len;
    start = to = (unsigned char *) safe_emalloc(3, len, 1);

    while (from < end) {
        c = *from++;

        if (c == ' ') {
            *to++ = '+';
#ifndef CHARSET_EBCDIC
        } else if ((c < '0' && c != '-' && c != '.') ||
                   (c < 'A' && c > '9') ||
                   (c > 'Z' && c < 'a' && c != '_') ||
                   (c > 'z')) {
            to[0] = '%';
            to[1] = hexchars[c >> 4];
            to[2] = hexchars[c & 15];
            to += 3;
#else /*CHARSET_EBCDIC*/
        } else if (!isalnum(c) && strchr("_-.", c) == NULL) {
            /* Allow only alphanumeric chars and '_', '-', '.'; escape the rest */
            to[0] = '%';
            to[1] = hexchars[os_toascii[c] >> 4];
            to[2] = hexchars[os_toascii[c] & 15];
            to += 3;
#endif /*CHARSET_EBCDIC*/
        } else {
            *to++ = c;
        }
    }
    *to = 0;
    if (new_length) {
        *new_length = to - start;
    }
    return (char *) start;
}

void JNICALL Java_com_xingcloud_analytic_xnative_XCNative_initCurl
  (JNIEnv *env, jclass thiz, jstring init)
{
	 curl_global_init(CURL_GLOBAL_ALL);
}

jint JNICALL Java_com_xingcloud_analytic_xnative_XCNative_sendReport
  (JNIEnv *env, jclass thiz, jstring url_params)
  {
	CURL *curl = NULL;
	//CURL *curl = curl_easy_init();
	CURLcode res;
	const char* pParams = env->GetStringUTFChars(url_params, 0);
	//size_t *olen;

	///const char
   //   __android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	struct MemoryStruct chunk;
	chunk.data=NULL;
	chunk.size = 0;
	curl = curl_easy_init();
	if(NULL == curl)
	{
		env->ReleaseStringUTFChars(url_params,pParams);
		return 0;
	}
	//int *new_length;
	//const char* encodeUrl = php_url_encode(pParams,strlen(pParams),new_length);//curl_easy_escape(curl,pParams,strlen(pParams));
//	char *url;//[1024]; //= baseUrl;
//	url = (char*)malloc(strlen(baseUrl)+strlen(customParam));
//	strcpy(url,baseUrl);
//
//	strcat(url,customParam);
	//strcat(url,pParams);
//	char* sPtr;
//	sPtr = (char*)malloc(strlen(pParams)+strlen(url));
//	if(NULL != sPtr)
//	{
//		strcpy(sPtr,url);
//		strcat(sPtr,pParams);
//		__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",sPtr);
//	}

	__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	curl_easy_setopt(curl,CURLOPT_URL,pParams);
	curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1L);
    //  curl_easy_setopt(curl,CURLOPT_POSTFIELDSIZE,strlen(pParams));
     // curl_easy_setopt(curl,CURLOPT_POSTFIELDS,pParams);

	curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);

	res = curl_easy_perform(curl);
	if(NULL != chunk.data )
	{
		__android_log_write(ANDROID_LOG_DEBUG,"Tag2",chunk.data);
	}
	if(res)
	{
		//free(url);
		//free(sPtr);

		env->ReleaseStringUTFChars(url_params,pParams);
		pParams = NULL;
		curl_easy_cleanup(curl);
		return 0;
	}
	//jstring str = env->NewStringUTF(chunk.data);
	//jstring str = env->NewStringUTF("success");

	//free(url);
	//free(sPtr);
	env->ReleaseStringUTFChars(url_params,pParams);
			pParams = NULL;
	curl_easy_cleanup(curl);
	return 1;
  }

jint JNICALL Java_com_xingcloud_analytic_xnative_XCNative_sendPageReport
  (JNIEnv *env, jclass thiz, jstring url_params)
  {
	CURL *curl = NULL;
	CURLcode res;
	const char* pParams = env->GetStringUTFChars(url_params, 0);
	//__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	//size_t *olen;
	//const char* encodeUrl = url_encode(pParams);
	struct MemoryStruct chunk;
	chunk.data=NULL;
	chunk.size = 0;
	curl = curl_easy_init();
	if(NULL == curl)
	{
		env->ReleaseStringUTFChars(url_params,pParams);
		return 0;
	}
	//const char* encodeUrl = curl_easy_escape(curl,pParams,strlen(pParams));

//	char *url;//[1024]; //= baseUrl;
//	url = (char*)malloc(strlen(pageUrl));
//	strcpy(url,pageUrl);


	//strcat(url,pParams);
//	char* sPtr;
//	sPtr = (char*)malloc(strlen(pParams)+strlen(url));
//	if(NULL != sPtr)
//	{
//		strcpy(sPtr,url);
//		strcat(sPtr,pParams);
//		__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",sPtr);
//	}
	//__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",url);
	__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	curl_easy_setopt(curl,CURLOPT_URL,pParams);
	curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1L);

      //curl_easy_setopt(curl,CURLOPT_URL,url);
//      curl_easy_setopt(curl,CURLOPT_POSTFIELDSIZE,strlen(pParams));
//      curl_easy_setopt(curl,CURLOPT_POSTFIELDS,pParams);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);

	res = curl_easy_perform(curl);
	if(NULL != chunk.data )
	{
		__android_log_write(ANDROID_LOG_DEBUG,"Tag2",chunk.data);
	}

	if(res)
	{
		//free(url);
		//free(sPtr);
		env->ReleaseStringUTFChars(url_params,pParams);
				pParams = NULL;
		curl_easy_cleanup(curl);
		return 0;
	}
	//jstring str = env->NewStringUTF(chunk.data);
	//jstring str = env->NewStringUTF("success");
	//free(url);
	//	free(sPtr);
	env->ReleaseStringUTFChars(url_params,pParams);
			pParams = NULL;
	curl_easy_cleanup(curl);
	return 1;
  }

jint JNICALL Java_com_xingcloud_analytic_xnative_XCNative_sendBaseReport
  (JNIEnv *env, jclass thiz, jstring params,jstring timestamp)
{
    
	CURL *curl = NULL;
	CURLcode res;
	const char* pParams = env->GetStringUTFChars(params, 0);
	//size_t *olen;
	//const char* encodeUrl = url_encode(pParams);
   // __android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	//const char* sTimestamp = env->GetStringUTFChars(timestamp, 0);
  //  __android_log_write(ANDROID_LOG_DEBUG,"XingCloud",sTimestamp);
	struct MemoryStruct chunk;
	chunk.data=NULL;
	chunk.size = 0;
	curl = curl_easy_init();
	if(NULL == curl)
	{
		env->ReleaseStringUTFChars(params,pParams);
		return 0;
	}
	//const char* encodeUrl = curl_easy_escape(curl,pParams,strlen(pParams));

//	char *url;//[1024]; //= baseUrl;
//	url = (char*)malloc(strlen(baseUrl)+strlen(normalParam));
//	strcpy(url,baseUrl);
//	//strcat(url,sTimestamp);
//	strcat(url,normalParam);
	//strcat(url,pParams);

//	char* sPtr;
//	sPtr = (char*)malloc(strlen(pParams)+strlen(url));
//	if(NULL != sPtr)
//	{
//		strcpy(sPtr,url);
//		strcat(sPtr,pParams);
//		__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",sPtr);
//	}
	__android_log_write(ANDROID_LOG_DEBUG,"XingCloud",pParams);
	curl_easy_setopt(curl,CURLOPT_URL,pParams);
	curl_easy_setopt(curl, CURLOPT_NOSIGNAL, 1L);
	//curl_easy_setopt(curl,CURLOPT_POSTFIELDSIZE,strlen(pParams));
	//curl_easy_setopt(curl,CURLOPT_POSTFIELDS,pParams);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);

	res = curl_easy_perform(curl);
	if(NULL != chunk.data )
	{
		__android_log_write(ANDROID_LOG_DEBUG,"Tag4",chunk.data);
	}
	if(res)
	{
		//free(url);
		//free(sPtr);
		env->ReleaseStringUTFChars(params,pParams);
				pParams = NULL;
		curl_easy_cleanup(curl);
		return 0;
	}
	//jstring str = env->NewStringUTF(chunk.data);
	//jstring str = env->NewStringUTF("success");
	//free(url);
	//	free(sPtr);
	env->ReleaseStringUTFChars(params,pParams);
			pParams = NULL;
	curl_easy_cleanup(curl);
	return 1;
}
