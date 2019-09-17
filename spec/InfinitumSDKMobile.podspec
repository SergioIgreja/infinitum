

Pod::Spec.new do |spec|

  spec.name         = "InfinitumSDKMobile"
# Update here every time a new version is out.
  spec.version      = "0.0.8"
  spec.summary      = "SDK that connects to Infinitum"

  spec.homepage     = "https://github.com/infinitum-dev/mobile-sdk"

  spec.license      = "MIT"

  spec.author             = { "infinitum_dev" => "developers@infinitum.app" }

  spec.platform     = :ios, "11.0"

# Update here every time a new version is out.
  spec.source       = { :git => "https://github.com/infinitum-dev/mobile-sdk.git", :tag => "0.0.8" }

# Location of the .framework archive.
  spec.vendored_frameworks = 'lib/build/bin/ios/InfinitumSDKMobileReleaseFramework/InfinitumSDKMobile.framework'
end
