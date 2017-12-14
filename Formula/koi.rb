class Koi < Formula
  homepage ''
  desc 'Koi who a gradle plugin creator fast.'
  version '1.0.0'

  test do
  system /bin/"bash", "-c", "printf -v hello -- '%s'"
  end
end
