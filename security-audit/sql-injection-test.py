#!/usr/bin/env python3
"""
SQL Injection Attack Test Script
This script tests for SQL injection vulnerabilities in the MDB application
"""

import requests
import json
import sys
from urllib.parse import quote

# Configuration
BASE_URL = "http://localhost:8080"
API_URL = f"{BASE_URL}/api"

class SQLInjectionTester:
    def __init__(self):
        self.session = requests.Session()
        self.vulnerabilities = []
        
    def test_search_endpoint(self):
        """Test SQL injection in search functionality"""
        print("🔍 Testing SQL Injection in Search Endpoint...")
        
        # SQL injection payloads
        payloads = [
            "'; DROP TABLE forum_threads; --",
            "' OR '1'='1",
            "' UNION SELECT * FROM users --",
            "'; INSERT INTO users (email) VALUES ('hacker@evil.com'); --",
            "' OR 1=1 LIMIT 1 OFFSET 1 --",
            "'; UPDATE users SET email='hacked@evil.com' WHERE id=1; --",
            "%'; DROP TABLE forum_threads; --",
            "test' AND (SELECT COUNT(*) FROM users) > 0 --",
            "' OR SLEEP(5) --",
            "'; EXEC xp_cmdshell('dir'); --"
        ]
        
        for payload in payloads:
            try:
                # Test search threads endpoint
                response = self.session.get(
                    f"{API_URL}/forums/threads",
                    params={"search": payload},
                    timeout=10
                )
                
                # Check for SQL error messages
                if self.check_sql_error_indicators(response):
                    self.vulnerabilities.append({
                        "type": "SQL Injection",
                        "endpoint": "/api/forums/threads",
                        "payload": payload,
                        "response_code": response.status_code,
                        "evidence": "SQL error messages detected"
                    })
                    print(f"🚨 VULNERABILITY FOUND: SQL Injection with payload: {payload}")
                
                # Check for timing attacks
                if response.elapsed.total_seconds() > 4:
                    self.vulnerabilities.append({
                        "type": "SQL Injection (Time-based)",
                        "endpoint": "/api/forums/threads",
                        "payload": payload,
                        "response_time": response.elapsed.total_seconds(),
                        "evidence": "Unusual response time indicating possible time-based SQL injection"
                    })
                    print(f"🚨 TIMING VULNERABILITY: Payload took {response.elapsed.total_seconds()}s")
                    
            except requests.exceptions.Timeout:
                print(f"⏰ Timeout with payload: {payload} (possible DoS vulnerability)")
            except Exception as e:
                print(f"❌ Error testing payload {payload}: {e}")
    
    def check_sql_error_indicators(self, response):
        """Check response for SQL error indicators"""
        error_indicators = [
            "sql syntax",
            "mysql_fetch",
            "ora-",
            "postgresql",
            "sqlite_",
            "sqlstate",
            "constraint violation",
            "column.*doesn't exist",
            "table.*doesn't exist",
            "duplicate entry",
            "foreign key constraint",
            "syntax error",
            "unexpected token",
            "unterminated quoted string"
        ]
        
        response_text = response.text.lower()
        for indicator in error_indicators:
            if indicator in response_text:
                return True
        return False
    
    def test_like_query_injection(self):
        """Test specific LIKE query injection vulnerability"""
        print("🔍 Testing LIKE Query SQL Injection...")
        
        # Specific payloads for LIKE queries
        like_payloads = [
            "test%'; DROP TABLE forum_threads; --",
            "test' ESCAPE '\\' AND 1=1 --",
            "test%' AND (SELECT COUNT(*) FROM users) > 0 --",
            "test' AND SUBSTRING(@@version,1,1) = '5' --",
            "test%' UNION SELECT null,username,password FROM users --"
        ]
        
        for payload in like_payloads:
            try:
                # Test the search functionality that uses LIKE queries
                response = self.session.get(
                    f"{API_URL}/forums/threads",
                    params={"search": payload},
                    timeout=10
                )
                
                if self.check_sql_error_indicators(response):
                    self.vulnerabilities.append({
                        "type": "LIKE Query SQL Injection",
                        "endpoint": "/api/forums/threads (search)",
                        "payload": payload,
                        "response_code": response.status_code,
                        "evidence": "SQL error in LIKE query"
                    })
                    print(f"🚨 LIKE INJECTION FOUND: {payload}")
                    
            except Exception as e:
                print(f"❌ Error testing LIKE payload {payload}: {e}")
    
    def test_parameter_pollution(self):
        """Test HTTP Parameter Pollution"""
        print("🔍 Testing HTTP Parameter Pollution...")
        
        try:
            # Test multiple parameters with same name
            response = self.session.get(
                f"{API_URL}/forums/threads?page=0&page=1&size=10&size=100"
            )
            
            if response.status_code == 200:
                print("⚠️  HTTP Parameter Pollution might be possible")
                
        except Exception as e:
            print(f"❌ Error testing parameter pollution: {e}")
    
    def generate_report(self):
        """Generate vulnerability report"""
        print("\n" + "="*60)
        print("🔒 SQL INJECTION SECURITY AUDIT REPORT")
        print("="*60)
        
        if not self.vulnerabilities:
            print("✅ No SQL injection vulnerabilities detected!")
            print("✅ Application appears to be using parameterized queries correctly")
        else:
            print(f"🚨 FOUND {len(self.vulnerabilities)} VULNERABILITIES:")
            for i, vuln in enumerate(self.vulnerabilities, 1):
                print(f"\n{i}. {vuln['type']}")
                print(f"   Endpoint: {vuln['endpoint']}")
                print(f"   Payload: {vuln['payload']}")
                print(f"   Evidence: {vuln['evidence']}")
        
        # Save report to file
        with open("c:/Users/Root/Downloads/MDB/security-audit/sql-injection-report.json", "w") as f:
            json.dump(self.vulnerabilities, f, indent=2)
        
        return len(self.vulnerabilities) == 0

def main():
    print("🔒 Starting SQL Injection Security Test")
    print("Target: MDB Application")
    print("="*50)
    
    tester = SQLInjectionTester()
    
    # Run all tests
    tester.test_search_endpoint()
    tester.test_like_query_injection()
    tester.test_parameter_pollution()
    
    # Generate report
    is_secure = tester.generate_report()
    
    if not is_secure:
        print("\n🚨 SECURITY ALERT: SQL injection vulnerabilities found!")
        print("🔧 Immediate action required to fix these issues")
        sys.exit(1)
    else:
        print("\n✅ SQL injection tests passed")
        sys.exit(0)

if __name__ == "__main__":
    main()